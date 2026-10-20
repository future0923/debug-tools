/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.server.utils;

import groovy.lang.GroovyShell;
import io.github.future0923.debug.tools.base.classloader.GroovyScriptClassLoader;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;
import io.github.future0923.debug.tools.server.groovy.DebugToolsGroovyScript;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import org.codehaus.groovy.control.CompilerConfiguration;
import java.lang.instrument.*;
import java.net.URL;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 异步执行的真实动作；资源写入、字节码重定义和业务脚本分别给出证据。 */
public final class AgentOperationActions {
    private AgentOperationActions() { }
    @SuppressWarnings("unchecked")
    public static Object execute(Map<String,Object> request) throws Exception {
        String kind=(String)request.get("kind");
        ClassLoader loader=AllClassLoaderHttpHandler.getClassLoader((String)request.get("classLoaderIdentity"));
        if("GROOVY".equals(kind)) return groovy(loader,(String)request.get("script"));
        Map<String,String> input=(Map<String,String>)request.get("files");
        if(input==null || input.isEmpty() || input.size()>32) throw new IllegalArgumentException("Provide 1..32 files");
        Map<String,byte[]> bytes=new LinkedHashMap<>();
        if("COMPILE".equals(kind)) {
            try (DynamicCompiler compiler=new DynamicCompiler(loader)) {
                for(Map.Entry<String,String> file:input.entrySet()) { validateClassName(file.getKey());compiler.addSource(file.getKey(),file.getValue()); }
                bytes=compiler.buildByteCodes();
            }
            if(!Boolean.TRUE.equals(request.get("deploy"))) return map("compiledClasses",bytes.keySet(),"deployed",false);
        } else for(Map.Entry<String,String> file:input.entrySet()) {
            byte[] content=Base64.getDecoder().decode(file.getValue());
            if(content.length>1048576) throw new IllegalArgumentException("Single file exceeds 1 MiB");
            bytes.put(file.getKey(),content);
        }
        if("RESOURCES".equals(kind)) {
            PluginConfiguration configuration=PluginManager.getInstance().getPluginConfiguration(loader);
            URL[] roots=configuration==null ? null : configuration.getWatchResources();
            if(roots==null || roots.length!=1 || !"file".equals(roots[0].getProtocol())) throw new IllegalStateException("Exactly one configured file watchResources root is required");
            return writeResources(Paths.get(roots[0].toURI()),bytes);
        }
        if(!"COMPILE".equals(kind) && !"CLASSES".equals(kind)) throw new IllegalArgumentException("Unknown operation kind");
        Instrumentation instrumentation=DebugToolsBootstrap.INSTANCE==null?null:DebugToolsBootstrap.INSTANCE.getInstrumentation();
        return redefine(instrumentation,loader,bytes);
    }
    public static Map<String,Object> groovy(ClassLoader applicationLoader,String script) throws Exception {
        if(script==null || script.length()>262144) throw new IllegalArgumentException("Groovy script must not exceed 256 KiB");
        ClassLoader previous=Thread.currentThread().getContextClassLoader();
        GroovyScriptClassLoader loader=GroovyScriptClassLoader.isolated(AgentOperationActions.class.getClassLoader(),applicationLoader);
        CompilerConfiguration config=new CompilerConfiguration();config.setScriptBaseClass(DebugToolsGroovyScript.class.getName());
        GroovyShell shell=new GroovyShell(loader,config);
        try {
            Thread.currentThread().setContextClassLoader(loader);
            Object result=shell.evaluate(script);
            if(result==null) return map("resultType","NULL","value",null);
            if(result instanceof String || result instanceof Number || result instanceof Boolean || result instanceof Character) {
                String value=String.valueOf(result);return map("resultType","SIMPLE","value",value.substring(0,Math.min(16384,value.length())),"truncated",value.length()>16384);
            }
            String offset=RunResultDTO.genOffsetPathRandom(result);DebugToolsResultUtils.putCache(offset,result);
            return map("resultType","OBJECT","className",result.getClass().getName(),"offsetPath",offset);
        } finally { Thread.currentThread().setContextClassLoader(previous);shell.getClassLoader().close(); }
    }
    public static Map<String,Object> writeResources(Path configuredRoot,Map<String,byte[]> files) throws Exception {
        Path root=configuredRoot.toRealPath();
        Map<Path,byte[]> destinations=new LinkedHashMap<>();
        for(Map.Entry<String,byte[]> entry:files.entrySet()) {
            String name=entry.getKey();Path relative=Paths.get(name);
            if(relative.isAbsolute() || name.contains("\\") || name.endsWith(".class") || relative.normalize().startsWith("..")) throw new IllegalArgumentException("Resource must be a relative non-class path");
            Path destination=root.resolve(relative).normalize();
            if(!destination.startsWith(root) || destination.equals(root)) throw new IllegalArgumentException("Resource escapes root");
            Path cursor=root;
            for(Path part:root.relativize(destination)) { cursor=cursor.resolve(part);if(Files.isSymbolicLink(cursor)) throw new IllegalArgumentException("Resource symlink is not allowed"); }
            destinations.put(destination,entry.getValue());
        }
        List<Map<String,Object>> evidence=new ArrayList<>();
        for(Map.Entry<Path,byte[]> entry:destinations.entrySet()) {
            Path destination=entry.getKey();
            try {
                Files.createDirectories(destination.getParent());
                Path temporary=Files.createTempFile(destination.getParent(),".debugtools-",".tmp");
                try { Files.write(temporary,entry.getValue());Files.move(temporary,destination,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
                finally { Files.deleteIfExists(temporary); }
                byte[] actual=Files.readAllBytes(destination);
                evidence.add(map("path",root.relativize(destination).toString(),"written",Arrays.equals(actual,entry.getValue()),"sizeBytes",actual.length,"sha256",digest(actual)));
            } catch(Exception error) { evidence.add(map("path",root.relativize(destination).toString(),"written",false,"error",error.toString())); }
        }
        boolean complete=evidence.stream().allMatch(row->Boolean.TRUE.equals(row.get("written")));
        return map("state",complete?"FILES_WRITTEN":"PARTIAL_OR_FAILED","files",evidence,"frameworkReloadConfirmed",false);
    }
    /** 独立的重定义边界便于在专用 JVM 验证实际部署与 loader 隔离。 */
    static Map<String,Object> redefine(Instrumentation instrumentation,ClassLoader loader,Map<String,byte[]> bytes) throws Exception {
        if(bytes.isEmpty() || bytes.size()>128) throw new IllegalArgumentException("Expected 1..128 compiled classes");
        if(instrumentation==null || !instrumentation.isRedefineClassesSupported()) throw new IllegalStateException("Class redefinition is unavailable");
        Map<String,Class<?>> loaded=new HashMap<>();
        for(Class<?> type:instrumentation.getAllLoadedClasses()) if(type.getClassLoader()==loader && bytes.containsKey(type.getName())) loaded.put(type.getName(),type);
        List<ClassDefinition> definitions=new ArrayList<>();
        for(Map.Entry<String,byte[]> entry:bytes.entrySet()) {
            validateClassName(entry.getKey());Class<?> type=loaded.get(entry.getKey());
            if(type==null || !instrumentation.isModifiableClass(type)) throw new IllegalArgumentException("Class is not loaded/modifiable in selected loader: "+entry.getKey());
            definitions.add(new ClassDefinition(type,entry.getValue()));
        }
        synchronized(PluginManager.getInstance().getHotswapLock()) { instrumentation.redefineClasses(definitions.toArray(new ClassDefinition[0])); }
        return map("state","REDEFINED","classes",bytes.keySet(),"persistedToDisk",false);
    }
    private static void validateClassName(String name) { if(name==null || !name.matches("[\\p{javaJavaIdentifierStart}][\\p{javaJavaIdentifierPart}]*(\\.[\\p{javaJavaIdentifierStart}][\\p{javaJavaIdentifierPart}]*)*")) throw new IllegalArgumentException("Invalid fully-qualified class name"); }
    public static String digest(byte[] value) throws Exception { StringBuilder result=new StringBuilder();for(byte item:MessageDigest.getInstance("SHA-256").digest(value)) result.append(String.format("%02x",item));return result.toString(); }
}

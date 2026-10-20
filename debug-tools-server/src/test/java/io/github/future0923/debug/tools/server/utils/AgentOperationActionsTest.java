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
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AgentOperationActionsTest {
    /** 实际编译、定义、重定义；确认已有实例行为改变且错误 loader 不执行部署。 */
    @Test void compiledBytesRedefineOnlyAlreadyLoadedClassInSelectedLoader() throws Exception {
        String name="deployfixture.Sample";
        Map<String,byte[]> first;
        Map<String,byte[]> second;
        try(io.github.future0923.debug.tools.server.compiler.DynamicCompiler compiler=new io.github.future0923.debug.tools.server.compiler.DynamicCompiler(getClass().getClassLoader())) {
            compiler.addSource(name,"package deployfixture; public class Sample { public int value() { return 1; } }");first=compiler.buildByteCodes();
        }
        try(io.github.future0923.debug.tools.server.compiler.DynamicCompiler compiler=new io.github.future0923.debug.tools.server.compiler.DynamicCompiler(getClass().getClassLoader())) {
            compiler.addSource(name,"package deployfixture; public class Sample { public int value() { return 2; } }");second=compiler.buildByteCodes();
        }
        class IsolatedLoader extends ClassLoader { Class<?> define(byte[] bytes){return defineClass(name,bytes,0,bytes.length);} }
        IsolatedLoader loader=new IsolatedLoader();Class<?> type=loader.define(first.get(name));Object instance=type.getDeclaredConstructor().newInstance();
        assertEquals(1,type.getMethod("value").invoke(instance));
        java.lang.instrument.Instrumentation instrumentation=net.bytebuddy.agent.ByteBuddyAgent.install();
        assertThrows(IllegalArgumentException.class,()->AgentOperationActions.redefine(instrumentation,getClass().getClassLoader(),second));
        assertEquals(1,type.getMethod("value").invoke(instance));
        assertEquals("REDEFINED",AgentOperationActions.redefine(instrumentation,loader,second).get("state"));
        assertEquals(2,type.getMethod("value").invoke(instance));
    }

    @Test void groovyReturnsCorrelatedValueAndRestoresThreadLoaderOnException() throws Exception {
        ClassLoader original=Thread.currentThread().getContextClassLoader();
        Map<String,Object> result=AgentOperationActions.groovy(getClass().getClassLoader(),"6 * 7");
        assertEquals("42",result.get("value"));assertSame(original,Thread.currentThread().getContextClassLoader());
        assertThrows(Exception.class,()->AgentOperationActions.groovy(getClass().getClassLoader(),"throw new IllegalStateException('test')"));
        assertSame(original,Thread.currentThread().getContextClassLoader());
    }
    @Test void writesResourceBytesAndRejectsTraversalBeforeAnyMutation() throws Exception {
        Path root=Files.createTempDirectory("resource-test");
        try {
            Map<String,byte[]> files=new LinkedHashMap<>();files.put("nested/app.yml","hello".getBytes("UTF-8"));
            Map<String,Object> result=AgentOperationActions.writeResources(root,files);
            assertEquals("FILES_WRITTEN",result.get("state"));assertEquals(false,result.get("frameworkReloadConfirmed"));
            assertEquals("hello",new String(Files.readAllBytes(root.resolve("nested/app.yml")),"UTF-8"));
            files.put("../escape.txt",new byte[0]);
            assertThrows(IllegalArgumentException.class,()->AgentOperationActions.writeResources(root,files));
        } finally { try(java.util.stream.Stream<Path> paths=Files.walk(root)) { paths.sorted(Comparator.reverseOrder()).forEach(path->{try {Files.delete(path);}catch(Exception error){throw new RuntimeException(error);}}); } }
    }
}

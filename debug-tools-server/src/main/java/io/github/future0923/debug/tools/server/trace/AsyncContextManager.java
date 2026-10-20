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
package io.github.future0923.debug.tools.server.trace;

import io.github.future0923.debug.tools.base.trace.InvocationContext;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 显式选定应用 Executor 的 execute(Runnable) 提交点；不改 bootstrap 线程池或全局调度器。 */
public final class AsyncContextManager implements AutoCloseable {
    private final Map<String,Entry> entries=new LinkedHashMap<>();
    public synchronized Map<String,Object> start(Instrumentation instrumentation,Class<?> type) throws Exception {
        if(instrumentation==null || !instrumentation.isRetransformClassesSupported() || !instrumentation.isModifiableClass(type))throw new IllegalStateException("Retransformation unavailable");
        if(type.getClassLoader()==null || Class.forName(InvocationContext.class.getName(),false,type.getClassLoader())!=InvocationContext.class)throw new IllegalArgumentException("Application loader with visible InvocationContext required");
        if(!java.util.concurrent.Executor.class.isAssignableFrom(type))throw new IllegalArgumentException("Target must implement Executor");
        Method method=type.getDeclaredMethod("execute",Runnable.class);
        if(method.getReturnType()!=void.class || Modifier.isAbstract(method.getModifiers()) || Modifier.isNative(method.getModifiers()))throw new IllegalArgumentException("Declared concrete execute(Runnable) required");
        for(Entry entry:entries.values())if(entry.type==type)return describe(entry);
        if(entries.size()>=16)throw new IllegalStateException("Async context target capacity is 16");
        Entry entry=new Entry(UUID.randomUUID().toString(),type,instrumentation);entries.put(entry.id,entry);
        AtomicBoolean transformed=new AtomicBoolean();AtomicReference<String> error=new AtomicReference<>();
        try {
            entry.transformer=new AgentBuilder.Default().disableClassFormatChanges().with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override public void onTransformation(TypeDescription description,ClassLoader loader,JavaModule module,boolean loaded,DynamicType dynamicType){transformed.set(true);}
                    @Override public void onError(String name,ClassLoader loader,JavaModule module,boolean loaded,Throwable failure){error.compareAndSet(null,failure.toString());}
                }).type(named(type.getName()),is(type.getClassLoader()))
                .transform((builder,description,loader,module,domain)->builder.visit(Advice.to(SubmitAdvice.class).on(named("execute").and(takesArguments(Runnable.class)).and(returns(void.class)))))
                .installOn(instrumentation);
            if(!transformed.get() || error.get()!=null)throw new IllegalStateException("Transformation unconfirmed: "+error.get());
            return describe(entry);
        } catch(Exception | LinkageError failure) {
            Map<String,Object> cleanup=stop(entry.id);
            throw new IllegalStateException("Async context start failed; cleanup="+cleanup,failure);
        }
    }
    public synchronized List<Map<String,Object>> list(){List<Map<String,Object>> result=new ArrayList<>();for(Entry entry:entries.values())result.add(describe(entry));return result;}
    public synchronized Map<String,Object> stop(String id){
        Entry entry=entries.get(id);if(entry==null)return map("contextId",id,"state","UNKNOWN_OR_STOPPED");
        try {
            if(entry.transformer!=null && !entry.transformer.reset(entry.instrumentation,AgentBuilder.RedefinitionStrategy.RETRANSFORMATION))throw new IllegalStateException("Transformer reset returned false");
            entries.remove(id);return map("contextId",id,"state","STOPPED","queuedTasksRetainCapturedContext",true);
        } catch(Exception | LinkageError failure){entry.cleanupError=failure.toString();return describe(entry);}
    }
    private static Map<String,Object> describe(Entry entry){return map("contextId",entry.id,"state",entry.cleanupError==null?"INSTALLED":"CLEANUP_FAILED","className",entry.type.getName(),"classLoaderIdentity",Integer.toHexString(System.identityHashCode(entry.type.getClassLoader())),"scope","SELECTED_EXECUTE_RUNNABLE","cleanupError",entry.cleanupError,"taskIdentityWrapped",true,"businessMdcPropagation",false);}
    @Override public synchronized void close(){for(String id:new ArrayList<>(entries.keySet()))stop(id);}
    /** Advice 只引用 base，保持应用 ClassLoader 可见性；无上下文时不更换 Runnable。 */
    public static class SubmitAdvice {
        @Advice.OnMethodEnter(suppress=Throwable.class)
        public static void enter(@Advice.Argument(value=0,readOnly=false) Runnable task){if(task!=null && InvocationContext.currentId()!=null)task=InvocationContext.wrap(task);}
    }
    private static final class Entry {final String id;final Class<?> type;final Instrumentation instrumentation;ResettableClassFileTransformer transformer;String cleanupError;Entry(String id,Class<?> type,Instrumentation instrumentation){this.id=id;this.type=type;this.instrumentation=instrumentation;}}
}

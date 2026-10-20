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

import io.github.future0923.debug.tools.base.trace.MethodWatchBuffer;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 每个观察绑定已加载的类与 loader；数量/时间到期后自动撤销 transformer。 */
public final class MethodWatchManager implements AutoCloseable {
    private final Map<String,Watch> watches=new LinkedHashMap<>();
    private final ScheduledExecutorService cleanup=Executors.newSingleThreadScheduledExecutor(task->{Thread thread=new Thread(task,"DebugTools-watch-cleanup");thread.setDaemon(true);return thread;});
    public MethodWatchManager() { cleanup.scheduleWithFixedDelay(this::expire,1,1,TimeUnit.SECONDS); }
    public synchronized Map<String,Object> start(Instrumentation instrumentation,Class<?> type,String methodName,String descriptor,int maxCalls,int durationMillis) throws Exception {
        if(instrumentation==null || !instrumentation.isRetransformClassesSupported() || !instrumentation.isModifiableClass(type))throw new IllegalStateException("Retransformation is unavailable");
        if(maxCalls<1 || maxCalls>100 || durationMillis<100 || durationMillis>300000)throw new IllegalArgumentException("Invalid observation bounds");
        if(watches.size()>=16)throw new IllegalStateException("Release an old watch; capacity is 16");
        if(Class.forName(MethodWatchBuffer.class.getName(),false,type.getClassLoader())!=MethodWatchBuffer.class)throw new IllegalStateException("Watch buffer is not visible to target ClassLoader");
        Method selected=null;
        for(Method method:type.getDeclaredMethods())if(method.getName().equals(methodName) && new MethodDescription.ForLoadedMethod(method).getDescriptor().equals(descriptor))selected=method;
        if(selected==null || Modifier.isAbstract(selected.getModifiers()) || Modifier.isNative(selected.getModifiers()))throw new IllegalArgumentException("Concrete method descriptor is required");
        for(Watch watch:watches.values())if(watch.active && watch.type==type && watch.method.equals(methodName) && watch.descriptor.equals(descriptor))throw new IllegalArgumentException("This exact method already has an active watch");
        String id=UUID.randomUUID().toString();
        Watch watch=new Watch(id,instrumentation,type,methodName,descriptor);watches.put(id,watch);
        MethodWatchBuffer.create(id,maxCalls,System.currentTimeMillis()+durationMillis);
        java.util.concurrent.atomic.AtomicReference<String> errorMessage=new java.util.concurrent.atomic.AtomicReference<>();
        java.util.concurrent.atomic.AtomicBoolean transformed=new java.util.concurrent.atomic.AtomicBoolean();
        try {
            watch.transformer=new AgentBuilder.Default().disableClassFormatChanges().with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override public void onTransformation(TypeDescription description,ClassLoader loader,JavaModule module,boolean loaded,DynamicType dynamicType) { if(description.getName().equals(type.getName()))transformed.set(true); }
                    @Override public void onError(String name,ClassLoader loader,JavaModule module,boolean loaded,Throwable error) { errorMessage.compareAndSet(null,name+": "+error); }
                }).type(named(type.getName()),is(type.getClassLoader()))
                .transform((builder,description,loader,module,domain)->builder.visit(Advice.withCustomMapping().bind(MethodWatchAdvice.WatchId.class,id).to(MethodWatchAdvice.class).on(named(methodName).and(hasDescriptor(descriptor)))))
                .installOn(instrumentation);
            watch.active=true;
            if(errorMessage.get()!=null || !transformed.get())throw new IllegalStateException("Watch transformation unconfirmed: "+errorMessage.get());
            watch.active=true;
            return map("watchId",id,"state","INSTALLED","className",type.getName(),"methodName",methodName,"descriptor",descriptor,"maxCalls",maxCalls,"durationMillis",durationMillis,"observedCalls",0);
        } catch(Exception | LinkageError failure) {
            // 注册后清理失败时保留 ID，调用方可 LIST/STOP 重试，不能隐藏仍存在的 transformer。
            watch.active=watch.transformer!=null;
            Map<String,Object> cleanupResult=stop(id,true);
            if(watch.active)throw new IllegalStateException("Watch start failed; cleanup pending for watchId="+id+": "+cleanupResult,failure);
            throw failure;
        }
    }
    /** 启动响应丢失时可列出已有观察，不能盲目再次插桩。 */
    public synchronized List<Map<String,Object>> list() {
        List<Map<String,Object>> result=new ArrayList<>();
        for(Watch watch:watches.values())result.add(map("watchId",watch.id,"className",watch.type.getName(),"classLoaderIdentity",Integer.toHexString(System.identityHashCode(watch.type.getClassLoader())),"methodName",watch.method,"descriptor",watch.descriptor,"transformerActive",watch.active,"cleanupError",watch.cleanupError));
        return result;
    }
    public synchronized Map<String,Object> read(String id,long cursor,int limit) {
        if(cursor<0 || limit<1 || limit>100)throw new IllegalArgumentException("Invalid watch page");
        Watch watch=watches.get(id);Map<String,Object> data=MethodWatchBuffer.snapshot(id,cursor,limit);
        data.put("transformerActive",watch!=null && watch.active);data.put("cleanupError",watch==null?null:watch.cleanupError);return data;
    }
    public synchronized Map<String,Object> stop(String id,boolean release) {
        Watch watch=watches.get(id);if(watch==null)return map("watchId",id,"state","UNKNOWN_OR_EXPIRED");
        MethodWatchBuffer.stop(id);
        if(watch.active) {
            try { if(!watch.transformer.reset(watch.instrumentation,AgentBuilder.RedefinitionStrategy.RETRANSFORMATION))throw new IllegalStateException("Transformer reset returned false");watch.active=false;watch.cleanupError=null; }
            catch(Exception error) { watch.cleanupError=error.toString(); }
        }
        if(release && !watch.active){watches.remove(id);MethodWatchBuffer.release(id);}
        return map("watchId",id,"state",watch.active?"CLEANUP_FAILED":release?"RELEASED":"STOPPED","transformerActive",watch.active,"cleanupError",watch.cleanupError);
    }
    private synchronized void expire() { try { for(Watch watch:new ArrayList<>(watches.values()))if(watch.active && MethodWatchBuffer.exhausted(watch.id))stop(watch.id,false); } catch(Throwable ignored) { /* 下次清理继续重试，不影响业务线程。 */ } }
    @Override public synchronized void close() { cleanup.shutdownNow();for(String id:new ArrayList<>(watches.keySet()))stop(id,true); }
    private static final class Watch {
        final String id,method,descriptor;final Instrumentation instrumentation;final Class<?> type;ResettableClassFileTransformer transformer;boolean active;String cleanupError;
        Watch(String id,Instrumentation instrumentation,Class<?> type,String method,String descriptor){this.id=id;this.instrumentation=instrumentation;this.type=type;this.method=method;this.descriptor=descriptor;}
    }
}

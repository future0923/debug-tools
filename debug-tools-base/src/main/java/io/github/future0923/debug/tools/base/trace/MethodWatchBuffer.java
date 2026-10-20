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
package io.github.future0923.debug.tools.base.trace;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 插桩只记录有界标量快照，不保留业务对象、ClassLoader 或任意 toString 引用。 */
public final class MethodWatchBuffer {
    private static final Map<String,Session> SESSIONS=new ConcurrentHashMap<>();
    private MethodWatchBuffer() { }
    public static void create(String id,int maxCalls,long deadlineMillis) {
        if(maxCalls<1 || maxCalls>100)throw new IllegalArgumentException("maxCalls must be 1..100");
        SESSIONS.put(id,new Session(maxCalls,deadlineMillis));
    }
    public static long enter(String id,Object[] arguments) {
        Session session=SESSIONS.get(id);if(session==null)return 0;
        synchronized(session) {
            if(session.stopped || System.currentTimeMillis()>session.deadline || session.events.size()>=session.maxCalls)return 0;
            long sequence=session.events.size()+1;
            List<Object> args=new ArrayList<>();for(int i=0;i<Math.min(16,arguments.length);i++)args.add(value(arguments[i]));
            Map<String,Object> event=map("sequence",sequence,"cursor",++session.cursor,"timestamp",System.currentTimeMillis(),"threadId",Thread.currentThread().getId(),"invocationId",InvocationContext.currentId(),
                "arguments",args,"argumentsTruncated",arguments.length>16,"state","ENTERED","startNanos",System.nanoTime());
            session.events.add(event);return sequence;
        }
    }
    public static void exit(String id,long sequence,Object returned,Throwable error) {
        Session session=SESSIONS.get(id);if(session==null || sequence==0)return;
        synchronized(session) {
            Map<String,Object> event=session.events.get((int)sequence-1);
            long start=(Long)event.remove("startNanos");event.put("durationNanos",System.nanoTime()-start);
            event.put("cursor",++session.cursor);event.put("state",error==null?"RETURNED":"THREW");event.put("result",value(returned));
            event.put("exception",error==null?null:map("type",error.getClass().getName(),"message",cut(error.getMessage())));
        }
    }
    public static Map<String,Object> snapshot(String id,long afterCursor,int limit) {
        Session session=SESSIONS.get(id);if(session==null)return map("watchId",id,"state","UNKNOWN_OR_EXPIRED");
        synchronized(session) {
            List<Map<String,Object>> events=new ArrayList<>();
            List<Map<String,Object>> ordered=new ArrayList<>(session.events);
            ordered.sort(Comparator.comparingLong(event->(Long)event.get("cursor")));
            for(Map<String,Object> event:ordered)if((Long)event.get("cursor")>afterCursor && events.size()<limit) {
                Map<String,Object> copy=new LinkedHashMap<>(event);copy.remove("startNanos");events.add(copy);
            }
            return map("watchId",id,"state",session.stopped?"STOPPED":System.currentTimeMillis()>session.deadline?"EXPIRED":session.events.size()>=session.maxCalls?"LIMIT_REACHED":"OBSERVING",
                "events",events,"observedCalls",session.events.size(),"nextCursor",events.isEmpty()?afterCursor:events.get(events.size()-1).get("cursor"),"deadlineMillis",session.deadline,
                "coverage","SCALAR_ARGUMENTS_AND_RESULT_ONLY","returnEventMayArriveLater",true);
        }
    }
    public static boolean exhausted(String id) {
        Session session=SESSIONS.get(id);if(session==null)return true;
        synchronized(session) { return session.stopped || System.currentTimeMillis()>session.deadline || session.events.size()>=session.maxCalls; }
    }
    public static void stop(String id) { Session session=SESSIONS.get(id);if(session!=null)synchronized(session){session.stopped=true;} }
    public static void release(String id) { SESSIONS.remove(id); }
    private static Object value(Object value) {
        if(value==null)return null;
        if(value instanceof String)return cut((String)value);
        if(value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof Character)return String.valueOf(value);
        return map("className",value.getClass().getName(),"identity",Integer.toHexString(System.identityHashCode(value)),"expanded",false);
    }
    private static String cut(String text) { return text==null?null:text.substring(0,Math.min(1024,text.length())); }
    private static Map<String,Object> map(Object... values) { Map<String,Object> result=new LinkedHashMap<>();for(int i=0;i<values.length;i+=2)result.put((String)values[i],values[i+1]);return result; }
    private static final class Session { final int maxCalls;final long deadline;final List<Map<String,Object>> events=new ArrayList<>();boolean stopped;long cursor;Session(int maxCalls,long deadline){this.maxCalls=maxCalls;this.deadline=deadline;} }
}

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
package io.github.future0923.debug.tools.extension.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.aop.support.AopUtils;
import java.util.*;

/** 只读取定义和现有单例，不触发 lazy bean、FactoryBean 或业务 getter。 */
public final class SpringRuntimeDiagnostics {
    private SpringRuntimeDiagnostics() { }
    public static Map<String, Object> inspect(List<ApplicationContext> contexts, Map<String, Object> request) {
        String action = text(request, "action", "CONTEXTS");
        List<Map<String, Object>> candidates = new ArrayList<>();
        for (ApplicationContext context : contexts) candidates.add(map("contextId", identity(context), "id", safe(context.getId()),
            "displayName", safe(context.getDisplayName()), "active", !(context instanceof ConfigurableApplicationContext) || ((ConfigurableApplicationContext)context).isActive()));
        if ("CONTEXTS".equals(action)) return map("contexts", candidates);
        String id = text(request, "contextId", null);
        ApplicationContext selected = null;
        for (ApplicationContext context : contexts) if (identity(context).equals(id) || (id == null && contexts.size() == 1)) selected = context;
        if (selected == null) return map("state", contexts.isEmpty() ? "CONTEXT_UNAVAILABLE" : "CONTEXT_SELECTION_REQUIRED", "contexts", candidates);
        if ("BEANS".equals(action)) return beans(selected, text(request, "nameContains", ""), integer(request, "offset", 0, 0, 100000), integer(request, "limit", 50, 1, 200));
        if ("PROPERTY_SOURCES".equals(action)) {
            Object keys = request.get("keys");
            if (!(keys instanceof List) || ((List<?>)keys).isEmpty() || ((List<?>)keys).size() > 100) throw new IllegalArgumentException("Provide 1..100 keys");
            return properties(selected, (List<?>)keys);
        }
        throw new IllegalArgumentException("Unknown Spring diagnostic action");
    }
    private static Map<String, Object> beans(ApplicationContext context, String filter, int offset, int limit) {
        if (!(context instanceof ConfigurableApplicationContext)) return map("state", "BEAN_FACTORY_UNAVAILABLE", "contextId", identity(context));
        ConfigurableListableBeanFactory factory = ((ConfigurableApplicationContext)context).getBeanFactory();
        Set<String> names = new TreeSet<>(Arrays.asList(factory.getBeanDefinitionNames()));
        names.addAll(Arrays.asList(factory.getSingletonNames()));
        List<String> matches = new ArrayList<>();
        for (String name : names) if (name.contains(filter)) matches.add(name);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = Math.min(offset, matches.size()); i < Math.min(matches.size(), offset + limit); i++) {
            String name = matches.get(i);
            Object instance = factory.containsSingleton(name) ? factory.getSingleton(name) : null;
            Map<String, Object> row = map("name", name, "instantiatedSingleton", instance != null, "aliases", Arrays.asList(factory.getAliases(name)));
            if (factory.containsBeanDefinition(name)) {
                BeanDefinition definition = factory.getBeanDefinition(name);
                row.putAll(map("declaredClassName", definition.getBeanClassName(), "scope", definition.getScope(), "lazy", definition.isLazyInit(),
                    "primary", definition.isPrimary(), "factoryBeanName", definition.getFactoryBeanName(), "factoryMethodName", definition.getFactoryMethodName(),
                    "resource", safe(definition.getResourceDescription()), "role", definition.getRole()));
            }
            if (instance != null) row.putAll(map("runtimeClass", instance.getClass().getName(), "proxy", AopUtils.isAopProxy(instance),
                "jdkProxy", AopUtils.isJdkDynamicProxy(instance), "cglibProxy", AopUtils.isCglibProxy(instance),
                "targetClass", AopUtils.getTargetClass(instance).getName(), "identity", Integer.toHexString(System.identityHashCode(instance))));
            rows.add(row);
        }
        return map("state", "AVAILABLE", "contextId", identity(context), "beans", rows, "total", matches.size(), "offset", offset,
            "nextOffset", Math.min(matches.size(), offset + rows.size()), "hasMore", offset + rows.size() < matches.size(), "instantiatesBeans", false);
    }
    private static Map<String, Object> properties(ApplicationContext context, List<?> keys) {
        if (!(context.getEnvironment() instanceof ConfigurableEnvironment)) return map("state", "PROPERTY_SOURCES_UNAVAILABLE");
        ConfigurableEnvironment environment = (ConfigurableEnvironment)context.getEnvironment();
        List<PropertySource<?>> sources = new ArrayList<>();
        for (PropertySource<?> source : environment.getPropertySources()) flatten(source, sources, 0);
        List<Map<String, Object>> values = new ArrayList<>();
        int remainingEntries = 256;
        boolean entriesTruncated = false;
        for (Object keyValue : keys) {
            if (!(keyValue instanceof String) || ((String)keyValue).trim().isEmpty() || ((String)keyValue).length() > 512) throw new IllegalArgumentException("Invalid property key");
            String key = (String)keyValue;
            List<Map<String, Object>> chain = new ArrayList<>();
            boolean selectedValue = false;
            for (int rank = 0; rank < sources.size(); rank++) {
                if (remainingEntries == 0) { entriesTruncated = true; break; }
                PropertySource<?> source = sources.get(rank);
                try {
                    Object value = source.getProperty(key);
                    if (value != null) {
                        chain.add(map("source", safe(source.getName()), "sourceType", source.getClass().getName(), "precedence", rank,
                            "value", safe(value), "selected", !selectedValue));
                        selectedValue = true; remainingEntries--;
                    }
                } catch (RuntimeException error) { chain.add(map("source", safe(source.getName()), "precedence", rank, "error", error.getClass().getName())); remainingEntries--; }
            }
            String resolved = null; String error = null;
            try { resolved = environment.getProperty(key); } catch (RuntimeException failure) { error = failure.getClass().getName(); }
            values.add(map("key", key, "resolvedValue", safe(resolved), "resolutionError", error, "sources", chain));
        }
        return map("state", "AVAILABLE", "contextId", identity(context), "properties", values, "precedenceOrder", "LOWER_NUMBER_FIRST",
            "valueMaxChars", 4096, "sourceEntriesTruncated", entriesTruncated, "sourceLimit", 256, "sourceLimitReached", sources.size() >= 256);
    }
    private static void flatten(PropertySource<?> source, List<PropertySource<?>> result, int depth) {
        if (result.size() >= 256 || depth > 16) return;
        if (source instanceof CompositePropertySource) for (PropertySource<?> child : ((CompositePropertySource)source).getPropertySources()) flatten(child,result,depth+1);
        else result.add(source);
    }
    private static String identity(ApplicationContext context) { return Integer.toHexString(System.identityHashCode(context)); }
    private static String safe(Object value) { if (value == null) return null; String text = String.valueOf(value); return text.substring(0, Math.min(text.length(),4096)); }
    private static String text(Map<String,Object> request, String key, String fallback) { Object value = request.get(key); if (value == null) return fallback; if (!(value instanceof String)) throw new IllegalArgumentException(key + " must be string"); return (String)value; }
    private static int integer(Map<String,Object> request, String key, int fallback, int min, int max) {
        Object value = request.get(key); if (value == null) return fallback;
        if (!(value instanceof Number) || ((Number)value).doubleValue() != ((Number)value).intValue()) throw new IllegalArgumentException("Invalid " + key);
        int result = ((Number)value).intValue(); if (result < min || result > max) throw new IllegalArgumentException("Invalid " + key); return result;
    }
    private static Map<String,Object> map(Object... pairs) { Map<String,Object> result = new LinkedHashMap<>(); for(int i=0;i<pairs.length;i+=2) result.put((String)pairs[i],pairs[i+1]); return result; }
}

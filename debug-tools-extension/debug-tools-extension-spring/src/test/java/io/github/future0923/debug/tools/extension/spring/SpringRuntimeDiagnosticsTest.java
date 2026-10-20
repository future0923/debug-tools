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

import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.env.MapPropertySource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SpringRuntimeDiagnosticsTest {
    static int constructions;
    public static class LazyBean { public LazyBean() { constructions++; } }
    @Test void beanInspectionDoesNotInstantiateLazyDefinitions() {
        constructions = 0;
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            RootBeanDefinition definition = new RootBeanDefinition(LazyBean.class); definition.setLazyInit(true);
            context.registerBeanDefinition("lazyExample", definition); context.refresh();
            Map<String,Object> result = SpringRuntimeDiagnostics.inspect(Collections.singletonList(context), map("action","BEANS","nameContains","lazyExample"));
            Map<?,?> bean = (Map<?,?>)((List<?>)result.get("beans")).get(0);
            assertEquals(false,bean.get("instantiatedSingleton")); assertEquals(true,bean.get("lazy")); assertEquals(0,constructions);
        }
    }
    @Test void propertySourcesFollowRealPrecedenceAndResolvePlaceholders() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("low", map("service.url","lower","suffix","api")));
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("high", map("service.url","https://host/${suffix}")));
            context.refresh();
            Map<String,Object> result = SpringRuntimeDiagnostics.inspect(Collections.singletonList(context),map("action","PROPERTY_SOURCES","keys",Arrays.asList("service.url","missing")));
            List<?> properties = (List<?>) result.get("properties"); Map<?,?> value = (Map<?,?>)properties.get(0);
            assertEquals("https://host/api",value.get("resolvedValue"));
            List<?> sources = (List<?>)value.get("sources"); assertEquals(2,sources.size());
            assertEquals("high",((Map<?,?>)sources.get(0)).get("source")); assertEquals(true,((Map<?,?>)sources.get(0)).get("selected"));
            assertEquals(false,((Map<?,?>)sources.get(1)).get("selected")); assertNull(((Map<?,?>)properties.get(1)).get("resolvedValue"));
        }
    }
    @Test void multipleContextsRequireExplicitIdentity() {
        try (GenericApplicationContext first = new GenericApplicationContext(); GenericApplicationContext second = new GenericApplicationContext()) {
            Map<String,Object> result = SpringRuntimeDiagnostics.inspect(Arrays.asList(first,second),map("action","BEANS"));
            assertEquals("CONTEXT_SELECTION_REQUIRED",result.get("state")); assertEquals(2,((List<?>)result.get("contexts")).size());
        }
    }
    private static Map<String,Object> map(Object... values) { Map<String,Object> result=new LinkedHashMap<>(); for(int i=0;i<values.length;i+=2)result.put((String)values[i],values[i+1]);return result; }
}

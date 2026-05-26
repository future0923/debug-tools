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

import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DebugToolsResultUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void convertRunResultDTOExpandsRecordComponents() throws Exception {
        Object record = compileAndCreatePersonRecord();

        List<RunResultDTO> result = DebugToolsResultUtils.convertRunResultDTO(record, "root");

        Map<String, String> values = result.stream()
                .collect(Collectors.toMap(RunResultDTO::getName, RunResultDTO::getValue));
        assertEquals("Ada", values.get("name"));
        assertEquals("36", values.get("age"));
    }

    @Test
    void getValueByOffsetReadsNestedRecordComponentByName() throws Exception {
        Object wrapper = compileAndCreateWrapperRecord();

        Object data = DebugToolsResultUtils.getValueByOffset(wrapper, "data", "record.component");
        List<RunResultDTO> result = DebugToolsResultUtils.convertRunResultDTO(data, "root/data@record.component");

        Map<String, String> values = result.stream()
                .collect(Collectors.toMap(RunResultDTO::getName, RunResultDTO::getValue));
        assertEquals("Ada", values.get("name"));
        assertEquals("36", values.get("age"));
    }

    private Object compileAndCreatePersonRecord() throws Exception {
        Class<?> recordClass = compileRecordsAndLoadClass("Person");
        Constructor<?> constructor = recordClass.getDeclaredConstructor(String.class, int.class);
        return constructor.newInstance("Ada", 36);
    }

    private Object compileAndCreateWrapperRecord() throws Exception {
        Class<?> recordClass = compileRecordsAndLoadClass("Wrapper");
        Class<?> personClass = recordClass.getClassLoader().loadClass("Person");
        Object person = personClass.getDeclaredConstructor(String.class, int.class).newInstance("Ada", 36);
        Constructor<?> constructor = recordClass.getDeclaredConstructor(personClass);
        return constructor.newInstance(person);
    }

    private Class<?> compileRecordsAndLoadClass(String className) throws Exception {
        Path sourceDir = tempDir.resolve("src");
        Path outputDir = tempDir.resolve("classes");
        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir);
        Path personSourceFile = sourceDir.resolve("Person.java");
        Files.write(
                personSourceFile,
                "public record Person(String name, int age) {}".getBytes(StandardCharsets.UTF_8)
        );
        Path wrapperSourceFile = sourceDir.resolve("Wrapper.java");
        Files.write(
                wrapperSourceFile,
                "public record Wrapper(Person data) {}".getBytes(StandardCharsets.UTF_8)
        );

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(
                null,
                null,
                null,
                "-d",
                outputDir.toString(),
                personSourceFile.toString(),
                wrapperSourceFile.toString()
        );
        assertEquals(0, result);

        URL[] urls = new URL[]{outputDir.toUri().toURL()};
        URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());
        return classLoader.loadClass(className);
    }
}

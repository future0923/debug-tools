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
package io.github.future0923.debug.tools.extension.spring.method;

import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringParamConvertUtilsTest {

    @Test
    void jsonEntityConvertsFileAndMultipartFileFieldsFromStringPaths() throws Exception {
        File tempFile = File.createTempFile("debug-tools-param-", ".txt");
        tempFile.deleteOnExit();
        Method method = Target.class.getDeclaredMethod("upload", FileUploadDTO.class);
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetMethodContent(DebugToolsJsonUtils.toRunContentDTOMap("{\n" +
                "  \"fileUploadDTO\": {\n" +
                "    \"type\": \"" + RunContentType.JSON_ENTITY.getType() + "\",\n" +
                "    \"content\": {\n" +
                "      \"type\": \"zip\",\n" +
                "      \"file\": \"" + escapeJson(tempFile.getAbsolutePath()) + "\",\n" +
                "      \"multipartFile\": \"" + escapeJson(tempFile.getAbsolutePath()) + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}"));

        Object[] args = SpringParamConvertUtils.getArgs(method, runDTO);

        assertInstanceOf(FileUploadDTO.class, args[0]);
        FileUploadDTO dto = (FileUploadDTO) args[0];
        assertEquals("zip", dto.getType());
        assertNotNull(dto.getFile());
        assertEquals(tempFile.getAbsolutePath(), dto.getFile().getAbsolutePath());
        assertNotNull(dto.getMultipartFile());
        assertEquals(tempFile.getName(), dto.getMultipartFile().getOriginalFilename());
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static class Target {

        @SuppressWarnings("unused")
        void upload(FileUploadDTO fileUploadDTO) {
        }
    }

    public static class FileUploadDTO {

        private String type;

        private File file;

        private MultipartFile multipartFile;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public MultipartFile getMultipartFile() {
            return multipartFile;
        }

        public void setMultipartFile(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
        }
    }
}

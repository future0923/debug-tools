/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.test.simple;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.property.ExcelHeadProperty;
import com.alibaba.excel.util.ClassUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author future0923
 */
public class EasyExcelTest {

    public static class DTO {

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ExcelProperty(index = 3)
        private String name;
    }

    public static class Test {

        public static void test() throws IOException {
            extracted();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ClassUtils.removeThreadLocalCache();
        ExcelHeadProperty excelHeadProperty = new ExcelHeadProperty(null, null, null);
        Thread.sleep(1000000000L);
    }

    public static void extracted() throws IOException {
        String filePath = "/Users/weilai/Desktop/备份/5月3-8日钉钉报备记录.xlsx";
        EasyExcel.read(Files.newInputStream(Paths.get(filePath)), DTO.class, new AnalysisEventListener<DTO>() {

            @Override
            public void invoke(DTO dto, AnalysisContext analysisContext) {
                System.out.println(dto);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).doReadAll();
    }
}

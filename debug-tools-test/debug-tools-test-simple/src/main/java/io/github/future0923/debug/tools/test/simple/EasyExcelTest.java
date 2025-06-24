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

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

import cn.hutool.core.bean.BeanDescCache;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;

import java.lang.reflect.Field;

/**
 * @author future0923
 */
public class HuToolJson {

    public static class DTO {

        private String jiu;

        private Integer he;

        public Integer getHe() {
            return he;
        }

        public void setHe(Integer he) {
            this.he = he;
        }

        public String getJiu() {
            return jiu;
        }

        public void setJiu(String jiu) {
            this.jiu = jiu;
        }
    }

    public static class Test {
        public static String test() {
            DTO dto = new DTO();
            dto.setJiu("HuToolJson");
            dto.setHe(18);
            String jsonStr = JSONUtil.toJsonStr(dto);
            System.out.println(jsonStr);
            Field[] a = ReflectUtil.getFields(DTO.class);
            return jsonStr;
        }

    }

    public static void main(String[] args) throws InterruptedException {
        ReflectUtil.getFields(Test.class);
        BeanDescCache.INSTANCE.clear();
        Thread.sleep(1000000000L);
    }
}

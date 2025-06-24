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
package io.github.future0923.debug.tools.test.simple.json;

import cn.hutool.core.bean.BeanDescCache;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

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

        public static String node() throws JsonProcessingException {
            List<Node> list = Arrays.<Node>asList(new Node(new TestNode("testNode1", 1, null), "node1"), new Node(new TestNode("testNode2", 2,null), "node2"));
            TestNode testNode = new TestNode("testNode", 0, list);
            String jsonStr = JSONUtil.toJsonStr(testNode);
            System.out.println(jsonStr);
            JSONUtil.toBean(jsonStr, TestNode.class);
            return jsonStr;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReflectUtil.getFields(Test.class);
        BeanDescCache.INSTANCE.clear();
        Thread.sleep(1000000000L);
    }
}

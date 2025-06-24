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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class GsonTest {

    private static final Gson gson = new Gson();

    public static class DTO {

        private String name;

        private Integer age;

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Test {
        public static String test() {
            DTO dto = new DTO();
            dto.setName("Gson");
            dto.setAge(18);
            String jsonStr = gson.toJson(dto);
            System.out.println(jsonStr);
            return jsonStr;
        }

        public static String node() throws JsonProcessingException {
            List<Node> list = Arrays.<Node>asList(new Node(new TestNode("testNode1", 1, null), "node1"), new Node(new TestNode("testNode2", 2,null), "node2"));
            TestNode testNode = new TestNode("testNode", 0, list);
            String jsonStr = gson.toJson(testNode);
            System.out.println(jsonStr);
            gson.fromJson(jsonStr, TestNode.class);
            return jsonStr;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000000000L);
    }
}

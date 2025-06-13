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
package io.github.future0923.debug.tools.test.simple.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class JacksonTest {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static class DTO {

        private String name;

        private Integer test;

        public Integer getTest() {
            return test;
        }

        public void setTest(Integer test) {
            this.test = test;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }



    public static class Test {
        public static String test() throws JsonProcessingException {
            DTO dto = new DTO();
            dto.setName("Gson");
            dto.setTest(18);
            String jsonStr = objectMapper.writeValueAsString(dto);
            System.out.println(jsonStr);
            return jsonStr;
        }

        public static void node() throws JsonProcessingException {
            List<Node> list = Arrays.<Node>asList(new Node(new TestNode("testNode1", 1, null), "node1"), new Node(new TestNode("testNode2", 2,null), "node2"));
            TestNode testNode = new TestNode("testNode", 0, list);
            String jsonStr = objectMapper.writeValueAsString(testNode);
            objectMapper.readValue(jsonStr, TestNode.class);
            System.out.println(jsonStr);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000000000L);
    }
}

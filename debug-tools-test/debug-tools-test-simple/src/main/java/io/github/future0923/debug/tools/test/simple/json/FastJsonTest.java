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

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.List;

/**
 * @author future0923
 */
public class FastJsonTest {

    public static class DTO {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        //private Integer das;
        //
        //public Integer getDas() {
        //    return das;
        //}
        //
        //public void setDas(Integer das) {
        //    this.das = das;
        //}

        private Sub sub;

        public Sub getSub() {
            return sub;
        }

        public void setSub(Sub sub) {
            this.sub = sub;
        }
    }

    public static class Sub {
        private String subAge;

        public String getSubAge() {
            return subAge;
        }

        public void setSubAge(String subAge) {
            this.subAge = subAge;
        }
    }

    public static class Test {
        public static String test() {
            DTO dto = new DTO();
            dto.setName("FastJson");
            Sub sub = new Sub();
            sub.setSubAge("FastJsonSub");
            dto.setSub(sub);
            //dto.setDas(18);
            String jsonStr = JSON.toJSONString(dto);
            System.out.println(jsonStr);
            return jsonStr;
        }

        public static String node() throws JsonProcessingException {
            List<Node> list = Arrays.<Node>asList(new Node(new TestNode("testNode1", 1, null), "node1"), new Node(new TestNode("testNode2", 2,null), "node2"));
            TestNode testNode = new TestNode("testNode", 0, list);
            String jsonStr = JSON.toJSONString(testNode);
            System.out.println(jsonStr);
            JSON.parseObject(jsonStr, TestNode.class);
            return jsonStr;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Test.test();
        Thread.sleep(1000000000L);
    }
}

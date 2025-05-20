package io.github.future0923.debug.tools.test.simple;

import com.google.gson.Gson;

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

    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000000000L);
    }
}

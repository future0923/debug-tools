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

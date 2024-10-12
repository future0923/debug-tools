package io.github.future0923.debug.power.common.protocal.http;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author future0923
 */
@Data
public class AllClassLoaderRes implements Serializable {

    private String defaultIdentity;

    private Set<Item> itemList;

    @Data
    public static class Item {

        private String name;

        private String identity;

        public Item() {
        }

        public Item(ClassLoader classLoader) {
            this.name = classLoader.getClass().getName();
            this.identity = Integer.toHexString(System.identityHashCode(classLoader));
        }
    }




}

package io.github.future0923.debug.power.common.protocal.http;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author future0923
 */
@Data
public class AllClassLoaderRes implements Serializable {

    private String name;

    private String identity;

    public AllClassLoaderRes() {
    }

    public AllClassLoaderRes(ClassLoader classLoader) {
        this.name = classLoader.getClass().getName();
        this.identity = Integer.toHexString(System.identityHashCode(classLoader));
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AllClassLoaderRes)) return false;
        AllClassLoaderRes that = (AllClassLoaderRes) object;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}

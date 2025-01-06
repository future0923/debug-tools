package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean;

import org.apache.ibatis.session.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * MyBatis对象持有
 *
 * @author future0923
 */
public class MyBatisHolder {

    private static final Set<Configuration> configurations = new HashSet<>();

    public static void configuration(Object configuration) {
        configurations.add((Configuration) configuration);
    }

    public static Set<Configuration> getConfiguration() {
        return configurations;
    }
}

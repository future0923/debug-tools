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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusEntityReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusMapperReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils.MyBatisUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;

/**
 * @author future0923
 */
public class MyBatisPlusPatcher {

    private static final Logger logger = Logger.getLogger(MyBatisPlusPatcher.class);

    @Init
    static Scheduler scheduler;

    @OnClassLoadEvent(classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration")
    public static void patchMybatisConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        StringBuilder src = new StringBuilder("{");
        src.append(PluginManagerInvoker.buildInitializePlugin(MyBatisPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(MyBatisPlugin.class, "init", "org.mybatis.spring.SqlSessionFactoryBean.class.getClassLoader()", ClassLoader.class.getName()));
        src.append("}");
        CtConstructor[] constructors = ctClass.getConstructors();
        for (CtConstructor constructor : constructors) {
            constructor.insertAfter(src.toString());
        }
        CtMethod removeMappedStatementMethod = CtNewMethod.make("public void $$removeMappedStatement(String statementName) {" +
                "   if(mappedStatements.containsKey(statementName)){" +
                "       mappedStatements.remove(statementName);" +
                "   }" +
                "}", ctClass);
        ctClass.addMethod(removeMappedStatementMethod);
        CtMethod addMappedStatement = ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")});
        addMappedStatement.insertBefore("$$removeMappedStatement($1.getId());");
    }


    /**
     * 实际上是 com.baomidou.mybatisplus.core.MybatisConfiguration$StrictMap，将 $ 换成 . 也可以识别
     * 写 $ 时 com.baomidou.mybatisplus.core.MybatisConfiguration 主类就获取不到了，不知道为啥
     */
    @OnClassLoadEvent(classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration.StrictMap")
    public static void patchMybatisConfigurationStrictMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put", new CtClass[]{classPool.get(String.class.getName()), classPool.get(Object.class.getName())});
        method.insertBefore("if(containsKey($1)){" +
                "   remove($1);" +
                "}");
    }

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void redefineMyBatisClass(final Class<?> clazz, final byte[] bytes) {
        logger.debug("redefineMyBatisPlus, className:{}", clazz.getName());
        ClassLoader userClassLoader = MyBatisPlugin.getUserClassLoader();
        if (MyBatisUtils.isMyBatisPlus(userClassLoader)) {
            if (MyBatisUtils.isMyBatisPlusEntity(userClassLoader, clazz)) {
                scheduler.scheduleCommand(new MyBatisPlusEntityReloadCommand(userClassLoader, clazz), 1000);
            }
            if (MyBatisUtils.isMyBatisMapper(userClassLoader, clazz)) {
                scheduler.scheduleCommand(new MyBatisPlusMapperReloadCommand(userClassLoader, clazz, bytes, ""), 1000);
            }
        }
    }
}

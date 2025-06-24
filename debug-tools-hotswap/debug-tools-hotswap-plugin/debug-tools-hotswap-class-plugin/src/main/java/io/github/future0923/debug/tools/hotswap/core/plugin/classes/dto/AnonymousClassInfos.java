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
package io.github.future0923.debug.tools.hotswap.core.plugin.classes.dto;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * 匿名内部类的信息
 *
 * <p>在构造函数中搜索主类的所有匿名类并进行计算 superclass、interfaces、所有的方法签名、所有的字段签名。</p>
 * <p>根据使用的构造函数。这是通过来自 ClassLoader（当前加载状态）或通过 Javassist 的 ClassPool。</p>
 * <p>注意： ClassPool 使用 ClassLoader 上的 LoadClassPath，因此这些资源是通过 ClassLoader 解析的。</p>
 * <p>Javassist可以解析资源并返回资源文件中的原始字节码。</p>
 * <p>使用 {@link #mapPreviousState} 来创建旧状态和新状态之间的兼容转换映射。然后插件使用这个映射来交换类字节码，以保持热交换更改的兼容性。</p>
 */
public class AnonymousClassInfos {

    private static final Logger LOGGER = Logger.getLogger(AnonymousClassInfos.class);

    /**
     * 从这个索引开始为热重载创建合成匿名类，避免和已有的类冲突。(debug.tools.A$10000)
     */
    public static final int UNIQUE_CLASS_START_INDEX = 10000;

    /**
     * 修改延迟多少毫秒认为是相同的时间
     */
    private static final long ALLOWED_MODIFICATION_DELTA = 100;

    /**
     * 已经生成的匿名类计数器
     */
    static int uniqueClass = UNIQUE_CLASS_START_INDEX;

    /**
     * 之前的状态
     */
    AnonymousClassInfos previous;

    /**
     * 过渡类信息
     */
    Map<AnonymousClassInfo, AnonymousClassInfo> compatibleTransitions;

    /**
     * 上次修改的时间戳
     */
    long lastModifiedTimestamp = 0;

    /**
     * 主类名
     */
    String className;

    /**
     * 匿名内信息集合
     */
    List<AnonymousClassInfo> anonymousClassInfoList = new ArrayList<>();

    /**
     * 通过类加载器获取匿名类信息
     *
     * @param classLoader 类加载器
     * @param className   主类名
     */
    public AnonymousClassInfos(ClassLoader classLoader, String className) {
        this.className = className;
        try {
            // 使用反射调用已加载的类（而不是加载新的类）。
            Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[]{String.class});
            m.setAccessible(true);
            int i = 1;
            while (true) {
                Class<?> anonymous = (Class<?>) m.invoke(classLoader, className + "$" + i);
                if (anonymous == null) {
                    break;
                }
                anonymousClassInfoList.add(i - 1, new AnonymousClassInfo(anonymous));
                i++;
            }
        } catch (Exception e) {
            throw new Error("Unexpected error in checking loaded classes", e);
        }
    }

    /**
     * 通过 classPool 获取匿名类信息
     *
     * @param classPool classPool
     * @param className 主类名
     */
    public AnonymousClassInfos(ClassPool classPool, String className) {
        this.className = className;
        lastModifiedTimestamp = lastModified(classPool, className);
        List<CtClass> declaredClasses;
        try {
            CtClass ctClass = classPool.get(className);
            declaredClasses = Arrays.asList(ctClass.getNestedClasses());
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("Class " + className + " not found.");
        }
        int i = 1;
        while (true) {
            try {
                CtClass anonymous = classPool.get(className + "$" + i);
                // 跳过过时的类
                if (!declaredClasses.contains(anonymous)) {
                    break;
                }
                anonymousClassInfoList.add(i - 1, new AnonymousClassInfo(anonymous));
                i++;
            } catch (NotFoundException e) {
                break;
            } catch (Exception e) {
                throw new Error("Unable to create AnonymousClassInfo definition for class " + className + "$i", e);
            }
        }
        LOGGER.trace("Anonymous class '{}' scan finished with {} classes found", className, i - 1);
    }

    /**
     * 在 previous 和 anonymousClassInfoList 之间寻找映射. previous -> new.
     * 如果 previous 不存在，则创建一个合成类名
     */
    private void calculateCompatibleTransitions() {
        compatibleTransitions = new HashMap<>();
        List<AnonymousClassInfo> previousInfos = new ArrayList<>(previous.anonymousClassInfoList);
        List<AnonymousClassInfo> currentInfos = new ArrayList<>(anonymousClassInfoList);
        if (previousInfos.size() > currentInfos.size()) {
            if (currentInfos.isEmpty()) {
                previousInfos.clear();
            } else {
                previousInfos = previousInfos.subList(0, currentInfos.size());
            }
        }
        searchForMappings(compatibleTransitions, previousInfos, currentInfos, AnonymousClassInfo::matchExact);
        searchForMappings(compatibleTransitions, previousInfos, currentInfos, AnonymousClassInfo::matchSignatures);
        searchForMappings(compatibleTransitions, previousInfos, currentInfos, AnonymousClassInfo::matchClassSignature);
        // 需要定义多少个匿名类
        int newDefinitionCount = anonymousClassInfoList.size();
        // 最后一个匿名类的索引
        int lastAnonymousClassIndex = previous.anonymousClassInfoList.size();
        for (AnonymousClassInfo currentNotMatched : currentInfos) {
            if (lastAnonymousClassIndex < newDefinitionCount) {
                compatibleTransitions.put(new AnonymousClassInfo(className + "$" + (lastAnonymousClassIndex + 1)), currentNotMatched);
                lastAnonymousClassIndex++;
            } else {
                compatibleTransitions.put(new AnonymousClassInfo(className + "$" + uniqueClass++), currentNotMatched);
            }
        }
        if (LOGGER.isLevelEnabled(Logger.Level.TRACE)) {
            for (Map.Entry<AnonymousClassInfo, AnonymousClassInfo> mapping : compatibleTransitions.entrySet()) {
                LOGGER.trace("Transition {} => {}", mapping.getKey().getClassName(), mapping.getValue().getClassName());
            }
        }
    }

    /**
     * 遍历两个列表，使用匹配器查找匹配的匿名类。
     * 找到的匹配项将从之前和当前的列表中删除，并添加到转换列表中。
     */
    private void searchForMappings(Map<AnonymousClassInfo, AnonymousClassInfo> transitions,
                                   List<AnonymousClassInfo> previousInfos,
                                   List<AnonymousClassInfo> currentInfos,
                                   AnonymousClassInfoMatcher matcher) {
        for (ListIterator<AnonymousClassInfo> previousIt = previousInfos.listIterator(); previousIt.hasNext(); ) {
            AnonymousClassInfo previous = previousIt.next();
            for (ListIterator<AnonymousClassInfo> currentIt = currentInfos.listIterator(); currentIt.hasNext(); ) {
                AnonymousClassInfo current = currentIt.next();
                if (matcher.match(previous, current)) {
                    transitions.put(previous, current);
                    previousIt.remove();
                    currentIt.remove();
                    break;
                }
            }
        }
    }

    /**
     * 返回匿名类的存储信息
     *
     * @param className 匿名类名（e.g: Class$2）
     * @return 匿名类信息
     */
    public AnonymousClassInfo getAnonymousClassInfo(String className) {
        for (AnonymousClassInfo info : anonymousClassInfoList) {
            if (className.equals(info.getClassName())) {
                return info;
            }
        }
        return null;
    }

    /**
     * 设置 {@link #previous} 信息并计算需要兼容的转换
     * @param previousAnonymousClassInfos previous匿名类信息
     */
    public void mapPreviousState(AnonymousClassInfos previousAnonymousClassInfos) {
        this.previous = previousAnonymousClassInfos;
        // 只保留一个状态
        previousAnonymousClassInfos.previous = null;
        calculateCompatibleTransitions();
    }

    /**
     * 如果最后一次修改时间戳与当前类名的时间戳相同，则返回true。
     */
    public boolean isCurrent(ClassPool classPool) {
        return lastModifiedTimestamp >= lastModified(classPool, className) - ALLOWED_MODIFICATION_DELTA;
    }

    /**
     * 在主类文件中获取时间戳
     */
    private long lastModified(ClassPool classPool, String className) {
        URL url = classPool.find(className);
        if (url != null) {
            String file = url.getFile();
            return new File(file).lastModified();
        }
        return 0;
    }

    /**
     * 查找兼容的转换类名称
     */
    public String getCompatibleTransition(String className) {
        for (Map.Entry<AnonymousClassInfo, AnonymousClassInfo> transition : compatibleTransitions.entrySet()) {
            if (transition.getKey().getClassName().equals(className)) {
                return transition.getValue().getClassName();
            }
        }
        return null;
    }

    public Map<AnonymousClassInfo, AnonymousClassInfo> getCompatibleTransitions() {
        return compatibleTransitions;
    }


    @FunctionalInterface
    private interface AnonymousClassInfoMatcher {
        boolean match(AnonymousClassInfo previous, AnonymousClassInfo current);
    }

}

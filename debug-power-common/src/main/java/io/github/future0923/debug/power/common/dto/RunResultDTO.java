package io.github.future0923.debug.power.common.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author future0923
 */
@Data
public class RunResultDTO implements Serializable {

    public enum Type {
        ROOT,
        SIMPLE,
        PROPERTY,
        MAP,
        MAP_ENTRY,
        COLLECTION,
    }

    /**
     * 类型
     */
    private Type type;

    /**
     * 属性名
     */
    private String name;

    /**
     * 属性名是否是数组
     */
    private boolean nameArray;

    /**
     * 属性名类型
     */
    private String nameClassName;

    /**
     * 属性名唯一标识
     */
    private String nameIdentity;

    /**
     * 属性名数量
     */
    private Integer nameChildSize;

    /**
     * 属性值
     */
    private String value;

    /**
     * 属性值是否是数组
     */
    private boolean valueArray;

    /**
     * 属性值类名
     */
    private String valueClassName;

    /**
     * 属性值唯一标识
     */
    private String valueIdentity;

    /**
     * 属性值数量
     */
    private Integer valueChildSize;

    /**
     * 字段偏移量
     */
    private String filedOffset;

    /**
     * 是否还能展开
     */
    private boolean leaf;

    public RunResultDTO(Object name, Object value) {
        this(name, value, Type.ROOT, String.valueOf(System.identityHashCode(value)));
    }

    public RunResultDTO(Object name, Object value, Type type, String filedOffset) {
        this.nameIdentity = Integer.toHexString(System.identityHashCode(name));
        this.valueIdentity = Integer.toHexString(System.identityHashCode(value));
        this.type = type;
        this.filedOffset = filedOffset;
        if (name != null) {
            try {
                nameChildSize = CollUtil.size(name);
            } catch (Exception ignored) {
            }
            this.nameClassName = genClassName(name, nameChildSize);
            this.name = Convert.toStr(name);
        }
        this.nameArray = ArrayUtil.isArray(name);
        if (value != null) {
            try {
                valueChildSize = CollUtil.size(value);
            } catch (Exception ignored) {
            }
            this.valueClassName = genClassName(value, valueChildSize);
            this.value = Convert.toStr(value);
        }
        this.valueArray = ArrayUtil.isArray(value);
        this.leaf = value == null || ClassUtil.isBasicType(value.getClass()) || (ArrayUtil.isArray(value) && ClassUtil.isBasicType(value.getClass().getComponentType()));
    }

    /**
     * json序列化用
     */
    public boolean getNameArray() {
        return nameArray;
    }

    /**
     * json序列号用
     */
    public boolean getValueArray() {
        return valueArray;
    }

    /**
     * json序列号用
     */
    public boolean getLeaf() {
        return leaf;
    }


    public static String genOffsetPath(Object valueObj) {
        return String.valueOf(System.identityHashCode(valueObj));
    }

    private String genClassName(Object object, Integer childSize) {
        if (ArrayUtil.isArray(object)) {
            return object.getClass().getComponentType().getName() + "[" + childSize + "]";
        } else {
            return object.getClass().getName();
        }
    }
}

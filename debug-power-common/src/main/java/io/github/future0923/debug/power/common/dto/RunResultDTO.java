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
        COLLECTION,
    }

    /**
     * 唯一标识
     */
    private String identity;

    /**
     * 类名
     */
    private String className;

    /**
     * 类型
     */
    private Type type;

    /**
     * 属性名
     */
    private String name;

    /**
     * 属性值
     */
    private Object value;

    /**
     * 数量
     */
    private Integer childSize;

    /**
     * 字段偏移量
     */
    private String filedOffset;

    /**
     * 是否还能展开
     */
    private boolean leaf;

    public RunResultDTO(String name, Object valueObj) {
        this(name, valueObj, Type.ROOT, String.valueOf(System.identityHashCode(valueObj)));
    }

    public RunResultDTO(String name, Object valueObj, Type type, String filedOffset) {
        this.identity = Integer.toHexString(System.identityHashCode(valueObj));
        this.name = name;
        this.type = type;
        this.filedOffset = filedOffset;
        this.leaf = valueObj == null || ClassUtil.isBasicType(valueObj.getClass()) || (ArrayUtil.isArray(valueObj) && ClassUtil.isBasicType(valueObj.getClass().getComponentType()));
        if (valueObj == null) {
            this.value = "null";
        } else {
            try {
                childSize = CollUtil.size(valueObj);
            } catch (Exception e) {
                childSize = 0;
            }
            if (ArrayUtil.isArray(valueObj)) {
                this.className = valueObj.getClass().getComponentType().getName() + "[" + childSize +"]";
            } else {
                this.className = valueObj.getClass().getName();
            }
            this.value = Convert.toStr(valueObj);
        }
    }

    public boolean getLeaf() {
        return leaf;
    }

    public static String genOffsetPath(Object valueObj) {
        return String.valueOf(System.identityHashCode(valueObj));
    }
}

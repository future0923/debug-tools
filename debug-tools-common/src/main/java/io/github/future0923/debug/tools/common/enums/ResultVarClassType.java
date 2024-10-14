package io.github.future0923.debug.tools.common.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ResultVarClassType {

    INT("int"),
    OBJECT("object"),
    BOOLEAN("boolean"),
    BYTE("byte"),
    SHORT("short"),
    CHAR("char"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    COLLECTION("collection"),
    MAP("map"),
    MAP_ENTRY("map.entry"),
    ;
    private final String type;

    ResultVarClassType(String type) {
        this.type = type;
    }

    public static String getByClass(Class<?> clazz) {
        for (ResultVarClassType value : values()) {
            if (value.getType().equals(clazz.getName())) {
                return value.getType();
            }
        }
        return OBJECT.getType();
    }

    public static ResultVarClassType getByType(String type) {
        for (ResultVarClassType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return OBJECT;
    }
}

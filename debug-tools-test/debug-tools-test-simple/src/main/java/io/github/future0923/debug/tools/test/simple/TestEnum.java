package io.github.future0923.debug.tools.test.simple;

/**
 * @author future0923
 */
public enum TestEnum {

    A(11, "A1"),
    B(21, "B1"),
    C(3, "C"),
    ;

    private final Integer code;

    private final String name;

    TestEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Object getByCode() {
        return values();
    }

    @Override
    public String toString() {
        return "TestEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}

package io.github.future0923.debug.tools.sql;


import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;


public enum DataSourceDriverClassEnum {
    /**
     * mysql5.1以下
     */
    MYSQL5("mysql", "com.mysql.jdbc.NonRegisteringDriver", (sta, parameters) -> {
        String sql = sta.toString().replace("** BYTE ARRAY DATA **", "NULL");
        return sql.replace("com.mysql.jdbc.ClientPreparedStatement:", "");
    }),

    /**
     * mysql6.x及mysql8.x
     */
    MYSQL8("mysql", "com.mysql.cj.jdbc.NonRegisteringDriver", (sta, parameters) -> {
        String sql = sta.toString().replace("** BYTE ARRAY DATA **", "NULL");
        return sql.replace("com.mysql.cj.jdbc.ClientPreparedStatement:", "");
    }),
    /**
     * postgresql
     */
    POSTGRESQL("postgresql", "org.postgresql.Driver", (sta, parameters) -> sta.toString()),

    /**
     * sqlserver
     */
    SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", (sta, parameters) -> {
        Object[] inOutParam = (Object[]) ReflectUtil.getFieldValue(sta, "inOutParam");
        Object[] parameterValues = new Object[inOutParam.length];
        for (int i = 0; i < inOutParam.length; i++) {
            parameterValues[i] = ReflectUtil.invoke(inOutParam[i], "getSetterValue");
        }
        final String statementQuery = (String) ReflectUtil.getFieldValue(sta, "userSQL");
        return formatStringSql(statementQuery, parameterValues);
    }),
    /**
     * clickhouse
     */
    CLICKHOUSE("clickhouse", "ru.yandex.clickhouse.ClickHouseDriver", (sta, parameters) -> sta.toString()),

    /**
     * oracle
     */
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", (sta, parameters) -> {
        String statementQuery = ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(sta, "preparedStatement"), "sqlObject").toString();
        return formatStringSql(statementQuery, parameters);
    }),

    /**
     * dm
     */
    DM("dm", "dm.jdbc.driver.DmDriver", (sta, parameters) -> {
        String statementQuery = ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(sta, "rpstmt"), "originalSql").toString();
        return formatStringSql(statementQuery, parameters);
    }),

    /**
     * 默认。兜底使用
     */
    NONE("", "", (sta, parameters) -> "");

    DataSourceDriverClassEnum(String type, String className, SqlFormat format) {
        this.type = type;
        this.className = className;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public SqlFormat getFormat() {
        return format;
    }

    private final String type;
    private final String className;
    private final SqlFormat format;

    /**
     * 格式化sql
     * @param statementQuery  带有占位符的sql
     * @param parameterValues 参数值
     * @return 格式化后的sql
     */
    private static String formatStringSql(String statementQuery, Object[] parameterValues) {
        final StringBuilder sb = new StringBuilder();
        int currentParameter = 0;
        for (int pos = 0; pos < statementQuery.length(); pos++) {
            char character = statementQuery.charAt(pos);
            if (statementQuery.charAt(pos) == '?' && currentParameter <= parameterValues.length) {
                Object getSetterValue = parameterValues[currentParameter];
                if ("NULL".equals(getSetterValue)) {
                    sb.append("NULL"); // 输出 SQL NULL
                } else if (getSetterValue instanceof String) {
                    sb.append("'").append(getSetterValue).append("'");
                } else if (
                        getSetterValue instanceof Date
                                || getSetterValue instanceof LocalDateTime
                                || getSetterValue instanceof LocalDate
                                || getSetterValue instanceof LocalTime) {
                    sb.append("'").append(getSetterValue).append("'");
                } else {
                    sb.append(Convert.toStr(getSetterValue));
                }
                currentParameter++;
            } else {
                sb.append(character);
            }
        }
        return sb.toString();
    }

    /**
     * 构建数据源驱动枚举
     * @param className 驱动类
     * @return 数据驱动枚举
     */
    public static DataSourceDriverClassEnum of(String className) {
        if (StrUtil.isBlank(className)) {
            return DataSourceDriverClassEnum.NONE;
        }
        return Arrays
                .stream(DataSourceDriverClassEnum.values())
                .filter(driver -> StrUtil.equals(driver.getClassName(), className))
                .findFirst()
                .orElse(DataSourceDriverClassEnum.NONE);
    }

    /**
     * 是否为有效的数据库驱动
     * @param className 驱动类
     * @return 布尔值
     */
    public static boolean isTargetDriver(String className) {
        if (StrUtil.isBlank(className)) {
            return Boolean.FALSE;
        }
        return Arrays
                .stream(DataSourceDriverClassEnum.values())
                .anyMatch(driver -> StrUtil.equals(driver.getClassName(), className));
    }

    /**
     * 获取数据库类型，简称
     * @param className 驱动力
     * @return 数据库类型，如mysql
     */
    public static String getSqlDriverType(String className) {
        if (StrUtil.isBlank(className)) {
            return null;
        }
        return Arrays
                .stream(DataSourceDriverClassEnum.values())
                .filter(driver -> StrUtil.equals(driver.getClassName(), className))
                .findFirst()
                .orElse(NONE)
                .getType();
    }
}
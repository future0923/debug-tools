package io.github.future0923.debug.tools.sql;

import java.sql.Statement;

/**
 * sql格式化接口
 */
public interface SqlFormat {
    String format(Statement sta, Object[] parameters);
}
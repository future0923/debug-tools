package constants;

import java.util.Arrays;
import java.util.List;

public class SqlFormatConstant {
    /**
     * mysql 前缀替换白名单
     */
    public static final List<String> MYSQL_DRIVER_REPLACE_PREFIX_WHITE_LIST = Arrays.asList("com.mysql.jdbc.ClientPreparedStatement:",
            "com.mysql.jdbc.ServerPreparedStatement:", "com.mysql.cj.jdbc.ClientPreparedStatement:", "com.mysql.cj.jdbc.ServerPreparedStatement:");

}

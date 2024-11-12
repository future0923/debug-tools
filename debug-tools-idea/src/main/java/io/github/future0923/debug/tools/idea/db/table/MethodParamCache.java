package io.github.future0923.debug.tools.idea.db.table;

import lombok.Data;

import java.util.Map;

/**
 * @author future0923
 */
@Data
public class MethodParamCache {

    public static final String TABLE_NAME = "method_param_cache";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "method_identity TEXT," +
            "item_header TEXT," +
            "param_content TEXT," +
            "xxl_job_param TEXT" +
            ")";

    private Integer id;

    private String methodIdentity;

    private Map<String, String> itemHeaderMap;

    private String paramContent;

    private String xxlJobParam;

}

package io.github.future0923.debug.tools.idea.db.dao;

import io.github.future0923.debug.tools.idea.db.SQLiteUtils;
import io.github.future0923.debug.tools.idea.db.table.MethodParamCache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author future0923
 */
public class MethodParamDao {

    public List<MethodParamCache> getByMethodIdentity(String methodIdentity) {
        return SQLiteUtils.connection(connection -> {
            String sql = "select * from " + MethodParamCache.TABLE_NAME + " where method_identity = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, methodIdentity);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<MethodParamCache> result = new LinkedList<>();
            if (resultSet.next()) {
                MethodParamCache cache = new MethodParamCache();
                cache.setId(resultSet.getInt("id"));
                cache.setMethodIdentity(resultSet.getString("method_identity"));
                cache.setItemHeaderMap(resultSet.getString("item_header"));
                cache.setParamContent(resultSet.getString("param_content"));
                cache.setXxlJobParam(resultSet.getString("xxl_job_param"));
                result.add(cache);
            }
            return result;
        });
    }
}

package io.github.future0923.debug.tools.idea.db;

import io.github.future0923.debug.tools.idea.db.table.MethodParamCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author future0923
 */
public class SQLiteUtils {

    private static final String JDBC_DRIVER_URL = "jdbc:sqlite:debug-tools.db";

    public static void init() {
        connection(connection -> {
            Statement statement = connection.createStatement();
            statement.execute(MethodParamCache.CREATE_TABLE_SQL);
            statement.close();
        });
    }

    public static void connection(DbConsumer<Connection> consumer) {
        try (Connection connection = DriverManager.getConnection(JDBC_DRIVER_URL)) {
            consumer.accept(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T connection(DbFunction<Connection, T> consumer) {
        try (Connection connection = DriverManager.getConnection(JDBC_DRIVER_URL)) {
            return consumer.apply(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL" +
                ");";
        connection(connection -> {
            Statement statement = connection.createStatement();
            statement.execute(createTableSQL);

            String insertSQL = "INSERT INTO users (name) VALUES (?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, "weilai"); // 设置占位符的值
            preparedStatement.executeUpdate(); // 执行插入操作

            String querySQL = "SELECT id, name FROM users";
            ResultSet rs = connection.prepareStatement(querySQL).executeQuery();

            // 处理查询结果
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println("User ID: " + id + ", Name: " + name);
            }
        });
    }

    @FunctionalInterface
    public interface DbConsumer<T> {

        void accept(T t) throws Exception;
    }

    @FunctionalInterface
    public interface DbFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
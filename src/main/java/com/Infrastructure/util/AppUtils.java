package com.infrastructure.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class AppUtils {
    public static boolean executeQuery(DataSource dataSource, String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNull(Object o) {
        if (o instanceof String) {
            if (o == null ||
                    ((String) o).isEmpty() ||
                    ((String) o).isBlank() ||
                    ((String) o).length() == 0 ||
                    ((String) o).toLowerCase().trim().equals("null")
            )
                return true;
            return false;
        } else if (o instanceof List) {
            if (((List) o).isEmpty())
                return true;
        }
        return o == null ? true : false;
    }
}

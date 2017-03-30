package db_adapters;

import config.RestoreToolConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface DbAdapter {

    String getJdbcDbType();

    int getConflictsAmount(RestoreToolConfig config);

    void dropAllDbConnections(RestoreToolConfig config);

    void dropDb(RestoreToolConfig config);

    void createDb(RestoreToolConfig config);

    Process invokeRestoreProcess(RestoreToolConfig config);

    default Connection createConnection(RestoreToolConfig config) {
        try {
            return DriverManager.getConnection(String.format("jdbc:%s://%s:%s/", getJdbcDbType(), config.getDbHost(), config.getDbPort()),
                    config.getDbUser(),
                    config.getDbPassword());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

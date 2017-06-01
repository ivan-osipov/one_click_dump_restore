package db_adapters;

import config.RestoreToolConfig;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class PostgresAdapter implements DbAdapter {

    private static final PostgresAdapter INSTANCE = new PostgresAdapter();

    private static final String FETCH_CONFLICTS_AMOUNT_QUERY = "SELECT COUNT(*) FROM pg_stat_activity WHERE datname = ? and pid <> pg_backend_pid()";

    public static PostgresAdapter get() {
        return INSTANCE;
    }

    @Override
    public String getJdbcDbType() {
        return "postgresql";
    }

    @Override
    public void dropDb(RestoreToolConfig config) {
        try(Connection connection = createConnection(config)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP DATABASE IF EXISTS " + config.getDbName());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void createDb(RestoreToolConfig config) {
        try(Connection connection = createConnection(config)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE  " + config.getDbName());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void dropAllDbConnections(RestoreToolConfig config) {
        try (Connection connection = createConnection(config)) {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT pg_terminate_backend(pg_stat_activity.pid) " +
                            "FROM pg_stat_activity " +
                            "WHERE pg_stat_activity.datname = ? " +
                            "AND pid <> pg_backend_pid();");
            statement.setString(1, config.getDbName());
            statement.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public int getConflictsAmount(RestoreToolConfig config) {
        try (Connection connection = createConnection(config)) {
            if(connection == null) {
                throw new IllegalStateException("Db connection is not established. Check properties");
            }
            PreparedStatement statement = connection.prepareStatement(FETCH_CONFLICTS_AMOUNT_QUERY);
            statement.setString(1, config.getDbName());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public Process invokeRestoreProcess(RestoreToolConfig config) {
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(
                String.format("\"%spg_restore\"", config.getDbUtilsHome()),
                "--host=" + config.getDbHost(),
                "--port=" + config.getDbPort(),
                "--username=" + config.getDbUser(),
                "--no-password",
                "--dbname=" + config.getDbName(),
                config.getDumpPath()
        ));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        if (config.getDbPassword() != null) {
            pb.environment().put("PGPASSWORD", config.getDbPassword());
        }
        try {
            return pb.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

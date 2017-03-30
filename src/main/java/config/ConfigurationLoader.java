package config;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {

    public static final String PROPERTY_DB_TYPE = "db.type";
    public static final String PROPERTY_DB_HOST = "db.host";
    public static final String PROPERTY_DB_PORT = "db.port";
    public static final String PROPERTY_DB_USER = "db.user";
    public static final String PROPERTY_DB_PASSWORD = "db.password";
    public static final String PROPERTY_DB_NAME = "db.name";

    public static final String PROPERTY_DB_UTILS_PATH = "db.utils.home";

    private String filePath;

    public ConfigurationLoader(String filePath) {
        this.filePath = filePath;
    }

    public RestoreToolConfig load() {
        Properties properties = loadProperties(filePath);
        if(properties == null) {
            return null;
        }
        RestoreToolConfig config = new RestoreToolConfig();
        config.setDbType(properties.getProperty(PROPERTY_DB_TYPE));
        config.setDbHost(properties.getProperty(PROPERTY_DB_HOST));
        config.setDbPort(properties.getProperty(PROPERTY_DB_PORT));
        config.setDbUser(properties.getProperty(PROPERTY_DB_USER));
        config.setDbPassword(properties.getProperty(PROPERTY_DB_PASSWORD));
        config.setDbName(properties.getProperty(PROPERTY_DB_NAME));
        config.setDbUtilsHome(properties.getProperty(PROPERTY_DB_UTILS_PATH));
        return config;
    }

    private static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        try {
            ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();
            properties.load(classLoader.getResourceAsStream(filePath));
        } catch (IOException e) {
            System.err.println("Archive is damaged");
            return null;
        }
        return properties;
    }

}

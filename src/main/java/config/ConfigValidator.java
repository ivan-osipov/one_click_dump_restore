package config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigValidator {

    private static final String DUMP_PATH_PROPERTY_NAME = "dump path";

    private Set<String> supportedDbTypes;

    public ConfigValidator(Set<String> supportedDbTypes) {
        this.supportedDbTypes = new HashSet<>(supportedDbTypes);
    }

    public List<String> validate(RestoreToolConfig config) {
        List<String> errors = new ArrayList<>();
        require(config.getDumpPath(), errors, DUMP_PATH_PROPERTY_NAME);
        require(config.getDbType(), errors, ConfigurationLoader.PROPERTY_DB_TYPE);
        require(config.getDbHost(), errors, ConfigurationLoader.PROPERTY_DB_HOST);
        require(config.getDbPort(), errors, ConfigurationLoader.PROPERTY_DB_PORT);
        require(config.getDbUser(), errors, ConfigurationLoader.PROPERTY_DB_USER);
        require(config.getDbPassword(), errors, ConfigurationLoader.PROPERTY_DB_PASSWORD);
        require(config.getDbName(), errors, ConfigurationLoader.PROPERTY_DB_NAME);
        require(config.getDbUtilsHome(), errors, ConfigurationLoader.PROPERTY_DB_UTILS_PATH);

        if(!supportedDbTypes.contains(config.getDbType())) {
            errors.add(String.format("Unsupported %s: %s", ConfigurationLoader.PROPERTY_DB_TYPE, config.getDbType()));
        }

        return errors;
    }
    private void require(Object prop, List<String> errors, String name) {
        if(prop == null) {
            errors.add(String.format("Property %s not found", name));
        }
    }

}

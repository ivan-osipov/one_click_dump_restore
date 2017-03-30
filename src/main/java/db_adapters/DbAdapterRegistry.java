package db_adapters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DbAdapterRegistry {

    private Map<String, DbAdapter> adapters;

    public DbAdapterRegistry() {
        adapters = new HashMap<>();
    }

    public void register(String dbType, DbAdapter adapter) {
        adapters.put(dbType, adapter);
    }

    public Set<String> getRegisteredDbTypes() {
        return adapters.keySet();
    }

    public DbAdapter fetchByType(String dbType) {
        return adapters.get(dbType);
    }

}

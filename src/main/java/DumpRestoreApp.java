import config.ConfigValidator;
import config.ConfigurationLoader;
import config.RestoreToolConfig;
import db_adapters.DbAdapter;
import db_adapters.DbAdapterRegistry;
import db_adapters.PostgresAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class DumpRestoreApp {

    private static final String CONFIG_PATH = "app.properties";

    private static Scanner scan = new Scanner(System.in);

    private static DbAdapterRegistry dbAdapterRegistry;

    public static void main(String[] args) {
        System.out.println("=== Dump Restore App ===");
        initDbAdapterRegistry();
        launch(args);
        waitingPress();
    }

    private static void initDbAdapterRegistry() {
        dbAdapterRegistry = new DbAdapterRegistry();
        dbAdapterRegistry.register("POSTGRES", PostgresAdapter.get());
    }

    private static void launch(String[] args) {
        if (args.length == 0) {
            System.err.println("Dump not found");
            return;
        }

        String dumpPath = String.join("", Arrays.asList(args));
        if (fileDoesNotExists(dumpPath)) {
            return;
        }

        RestoreToolConfig config = new ConfigurationLoader(CONFIG_PATH).load();
        if (config == null) {
            return;
        }
        config.setDumpPath(dumpPath);

        Set<String> registeredDbTypes = dbAdapterRegistry.getRegisteredDbTypes();
        List<String> validationResult = new ConfigValidator(registeredDbTypes).validate(config);
        if (!validationResult.isEmpty()) {
            System.err.println("Config error");
            validationResult.forEach(System.err::println);
            return;
        }

        DbAdapter dbAdapter = dbAdapterRegistry.fetchByType(config.getDbType());

        boolean hasConflicts = findConflicts(dbAdapter, config);

        showConfirmation(config.getDbName(), () -> {
            if (hasConflicts) {
                dbAdapter.dropAllDbConnections(config);
            }
            reloadDumpByPath(dbAdapter, config);
        });
    }

    private static boolean fileDoesNotExists(String dumpPath) {
        Path path = Paths.get(dumpPath);
        return (path == null) || Files.notExists(path);
    }

    private static boolean findConflicts(DbAdapter dbAdapter, RestoreToolConfig config) {
        int conflictsAmount = dbAdapter.getConflictsAmount(config);

        if (conflictsAmount == 0) {
            System.out.println("Conflicting connections aren't found");
        } else {
            System.err.println(String.format("Found %s conflicting connections. They will be reset", conflictsAmount));
        }
        return conflictsAmount > 0;
    }

    private static void showConfirmation(String dbName, YesCallback yesCallback) {
        System.out.println("WARNING: Data base '" + dbName + "' will be deleted, created and restored with dump. Do you confirm? Y/N");
        String answer = scan.nextLine().toLowerCase();
        if ("y".equals(answer)) {
            try {
                yesCallback.call();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else if ("n".equals(answer)) {
            System.out.println("Ok! See you later");
        } else {
            System.out.println("Incorrect answer");
        }
    }

    private static void reloadDumpByPath(DbAdapter dbAdapter, RestoreToolConfig config) {
        System.out.println("Removing db: " + config.getDbName());
        dbAdapter.dropDb(config);
        System.out.println(config.getDbName() + " is removed");

        System.out.println("Creating db: " + config.getDbName());
        dbAdapter.createDb(config);
        System.out.println(config.getDbName() + " is created");

        Process process = dbAdapter.invokeRestoreProcess(config);
        System.out.println("Restoring: " + config.getDumpPath());
        try {
            process.waitFor();
            System.out.println("Done!");
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.err.println("Process ended with errors. Please restart");
        }
    }

    private static void waitingPress() {
        System.out.print("Press any key to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            //ignore
        }
    }

    private interface YesCallback {
        void call() throws Exception;
    }

}

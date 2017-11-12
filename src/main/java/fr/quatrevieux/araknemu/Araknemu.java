package fr.quatrevieux.araknemu;

import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.realm.RealmConfiguration;
import fr.quatrevieux.araknemu.realm.RealmLoader;
import fr.quatrevieux.araknemu.realm.RealmService;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Startup class
 */
public class Araknemu {
    final private Logger logger = LoggerFactory.getLogger(getClass());

    final private Configuration configuration;
    final private DatabaseHandler database;
    final private List<Service> services = new ArrayList<>();

    public Araknemu(Configuration configuration, DatabaseHandler database) {
        this.configuration = configuration;
        this.database = database;
    }

    /**
     * Boot all services
     */
    public void boot() {
        logger.info("Booting services");

        for (Service service : services) {
            service.boot();
        }

        logger.info("Araknemu started");
    }

    /**
     * Stop all services
     */
    public void shutdown() {
        logger.info("Shutdown requested...");

        for (Service service : services) {
            service.shutdown();
        }

        logger.info("Araknemu successfully stopped");
    }

    /**
     * Add a new service
     */
    public void add(Service service) {
        services.add(service);
    }

    /**
     * Get the application configuration
     */
    public Configuration configuration() {
        return configuration;
    }

    /**
     * Get the database handler
     */
    public DatabaseHandler database() {
        return database;
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) throws IOException, SQLException {
        Configuration configuration = new DefaultConfiguration(
            new IniDriver(
                new Ini(new File("config.ini"))
            )
        );

        Araknemu app = new Araknemu(
            configuration,
            new DefaultDatabaseHandler(
                configuration.module(DatabaseConfiguration.class)
            )
        );

        app.add(new RealmLoader(app).load());

        app.boot();

        Runtime.getRuntime().addShutdownHook(new Thread(app::shutdown));
    }
}

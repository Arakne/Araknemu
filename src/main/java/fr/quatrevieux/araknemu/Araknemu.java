package fr.quatrevieux.araknemu;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.LivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.WorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.GameModule;
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.connector.LocalModule;
import fr.quatrevieux.araknemu.realm.RealmModule;
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
    public void boot() throws BootException {
        logger.info("Booting services");

        for (Service service : services) {
            service.boot();
        }

        logger.info("Running garbage collector");
        System.gc();

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
    public static void main(String[] args) throws IOException, SQLException, ContainerException, BootException {
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

        Container realmContainer = makeRealmContainer(app);
        Container gameContainer  = makeGameContainer(app, realmContainer);

        app.add(realmContainer.get(RealmService.class));
        app.add(gameContainer.get(GameService.class));

        app.boot();

        Runtime.getRuntime().addShutdownHook(new Thread(app::shutdown));
    }

    static private Container makeRealmContainer(Araknemu app) throws SQLException {
        Container container = new ItemPoolContainer();

        container.register(new LivingRepositoriesModule(
            app.database().get("realm")
        ));
        container.register(new RealmModule(app));

        return container;
    }

    static private Container makeGameContainer(Araknemu app, Container realmContainer) throws SQLException {
        Container container = new ItemPoolContainer();

        container.register(new LivingRepositoriesModule(
            app.database().get("game")
        ));
        container.register(new WorldRepositoriesModule(
            app.database().get("game")
        ));
        container.register(new GameModule(app));
        container.register(new LocalModule(realmContainer));

        return container;
    }
}

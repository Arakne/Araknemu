package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.SimpleConnectionPool;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.RepositoriesModule;
import fr.quatrevieux.araknemu.network.LoggedIoHandler;
import fr.quatrevieux.araknemu.network.in.AggregatePacketParser;
import fr.quatrevieux.araknemu.network.in.DefaultDispatcher;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import org.apache.mina.core.service.IoHandler;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RealmModuleTest {
    @Test
    void instances() throws IOException, ContainerException {
        Configuration configuration = new DefaultConfiguration(
            new IniDriver(new Ini(new File("src/test/test_config.ini")))
        );

        Container container = new ItemPoolContainer();
        container.register(new RepositoriesModule());
        container.register(new RealmModule(
            new Araknemu(
                configuration,
                new DefaultDatabaseHandler(
                    configuration.module(DatabaseConfiguration.class)
                )
            )
        ));

        assertInstanceOf(RealmService.class, container.get(RealmService.class));
        assertInstanceOf(SimpleConnectionPool.class, container.get(ConnectionPool.class));
        assertInstanceOf(LoggedIoHandler.class, container.get(IoHandler.class));
        assertInstanceOf(RealmConfiguration.class, container.get(RealmConfiguration.class));
        assertInstanceOf(DefaultDispatcher.class, container.get(Dispatcher.class));
        assertInstanceOf(AggregatePacketParser.class, container.get(PacketParser.class));
        assertInstanceOf(AuthenticationService.class, container.get(AuthenticationService.class));

    }

    public void assertInstanceOf(Class clazz, Object obj) {
        assertTrue(clazz.isInstance(obj));
    }
}
/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.netty.NettyServer;
import fr.quatrevieux.araknemu.core.network.parser.AggregatePacketParser;
import fr.quatrevieux.araknemu.core.network.parser.DefaultDispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.authentication.password.Argon2Hash;
import fr.quatrevieux.araknemu.realm.authentication.password.PasswordManager;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.apache.logging.log4j.LogManager;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RealmModuleTest {
    @Test
    void instances() throws IOException, ContainerException, SQLException {
        Configuration configuration = new DefaultConfiguration(
            new IniDriver(new Ini(new File("src/test/test_config.ini")))
        );

        Araknemu app = new Araknemu(
            configuration,
            new DefaultDatabaseHandler(
                configuration.module(DatabaseConfiguration.class),
                LogManager.getLogger()
            )
        );

        Container container = new ItemPoolContainer();
        container.register(new RealmModule(app));
        container.register(new SqlLivingRepositoriesModule(app.database().get("realm")));

        assertInstanceOf(RealmService.class, container.get(RealmService.class));
        assertInstanceOf(SessionConfigurator.class, container.get(SessionFactory.class));
        assertInstanceOf(NettyServer.class, container.get(Server.class));
        assertInstanceOf(RealmConfiguration.class, container.get(RealmConfiguration.class));
        assertInstanceOf(DefaultDispatcher.class, container.get(Dispatcher.class));
        assertInstanceOf(AggregatePacketParser.class, container.get(PacketParser.class));
        assertInstanceOf(AuthenticationService.class, container.get(AuthenticationService.class));
        assertInstanceOf(HostService.class, container.get(HostService.class));
        assertInstanceOf(SessionLogService.class, container.get(SessionLogService.class));
        assertInstanceOf(PasswordManager.class, container.get(PasswordManager.class));
        assertInstanceOf(PlainTextHash.class, container.get(PlainTextHash.class));
        assertInstanceOf(Argon2Hash.class, container.get(Argon2Hash.class));
    }

    public void assertInstanceOf(Class clazz, Object obj) {
        assertTrue(clazz.isInstance(obj));
    }
}
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
import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.core.network.SessionHandler;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.host.GameConnector;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Arrays;

public class RealmBaseCase extends DatabaseTestCase {
    static public class SendingRequestStack {
        final public DummyChannel channel;

        public SendingRequestStack(DummyChannel channel) {
            this.channel = channel;
        }

        public void assertLast(Object packet) {
            assertLast(packet.toString());
        }

        public void assertLast(String packet) {
            Assertions.assertEquals(packet, channel.getMessages().peek().toString());
        }

        public void assertCount(int count) {
            Assertions.assertEquals(count, channel.getMessages().size());
        }

        public void assertAll(Object... packets) {
            Assertions.assertArrayEquals(
                Arrays.stream(packets).map(Object::toString).toArray(),
                channel.getMessages().stream().map(Object::toString).toArray()
            );
        }

        public void assertEmpty() {
            Assertions.assertTrue(channel.getMessages().isEmpty());
        }
    }

    static public class GameConnectorStub implements GameConnector {
        public boolean checkLogin;
        public String token;
        public AuthenticationAccount account;

        @Override
        public void checkLogin(AuthenticationAccount account, HostResponse<Boolean> response) {
            response.response(checkLogin);
            this.account = account;
        }

        @Override
        public void token(AuthenticationAccount account, HostResponse<String> response) {
            this.account = account;
            response.response(token);
        }
    }

    protected Container container;
    protected RealmConfiguration configuration;
    protected RealmService service;
    protected RealmSession session;
    protected DummyChannel channel;
    protected SendingRequestStack requestStack;
    protected SessionHandler<RealmSession> sessionHandler;
    protected Araknemu app;
    protected TestingDataSet dataSet;
    protected GameHost gameHost;
    protected GameConnectorStub connector;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        Configuration conf = new DefaultConfiguration(
            new IniDriver(new Ini(new File("src/test/test_config.ini")))
        );

        app = new Araknemu(
            conf,
            new DefaultDatabaseHandler(
                conf.module(DatabaseConfiguration.class)
            )
        );

        container = new ItemPoolContainer();
        container.register(new RealmModule(app));
        container.register(new SqlLivingRepositoriesModule(
            app.database().get("realm")
        ));

        service = container.get(RealmService.class);
        configuration = service.configuration();

        sessionHandler = container.get(SessionHandler.class);
        channel = new DummyChannel();
        session = sessionHandler.create(channel);
        requestStack = new SendingRequestStack(channel);

        dataSet = new TestingDataSet(container);
        dataSet
            .declare(Account.class, AccountRepository.class)
            .declare(Player.class, PlayerRepository.class)
        ;

        container.get(HostService.class).declare(
            gameHost = new GameHost(
                connector = new GameConnectorStub(),
                1,
                1234,
                "127.0.0.1"
            )
        );

        gameHost.setCanLog(true);
        gameHost.setState(GameHost.State.ONLINE);
    }

    @AfterEach
    void tearDown() throws ContainerException {
        dataSet.destroy();
    }

    public void assertClosed() {
        Assertions.assertFalse(channel.isAlive());
    }

    public void sendPacket(Object packet) throws Exception {
        sessionHandler.received(session, packet.toString());
    }
}

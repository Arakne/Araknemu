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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.parser.AggregatePacketParser;
import fr.quatrevieux.araknemu.core.network.parser.DefaultDispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.handler.CheckDofusVersion;
import fr.quatrevieux.araknemu.realm.handler.StartSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealmSessionConfiguratorTest extends RealmBaseCase {
    private RealmSessionConfigurator configurator;
    private Logger logger;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        configurator = new RealmSessionConfigurator(
            new DefaultDispatcher<>(new PacketHandler[]{
                new StartSession(),
                new CheckDofusVersion(configuration),
                new PacketHandler() {
                    @Override
                    public void handle(Object session, Packet packet) {

                    }

                    @Override
                    public Class packet() {
                        return Credentials.class;
                    }
                }
            }),
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            ),
            logger = Mockito.mock(Logger.class)
        );
    }

    @Test
    void opened() throws Exception {
        ConfigurableSession session = new ConfigurableSession(channel);
        RealmSession realmSession = new RealmSession(session);

        configurator.configure(session, realmSession);

        realmSession.receive(new SessionCreated());

        requestStack.assertLast("HC"+realmSession.key().key());
    }

    @Test
    void exceptionShouldCloseSession() {
        ConfigurableSession session = new ConfigurableSession(channel);
        RealmSession realmSession = new RealmSession(session);

        configurator.configure(session, realmSession);

        realmSession.exception(new RuntimeException());
        assertFalse(realmSession.isAlive());
    }

    @Test
    void exceptionCaughtRateLimit() {
        ConfigurableSession session = new ConfigurableSession(channel);
        RealmSession realmSession = new RealmSession(session);

        configurator.configure(session, realmSession);

        realmSession.exception(new RateLimitException());

        assertFalse(session.isAlive());
        Mockito.verify(logger).error(MarkerManager.getMarker("RATE_LIMIT"), "[{}] RateLimit : close session", realmSession);
    }

    @Test
    void exceptionShouldIgnoreConnectionReset() {
        ConfigurableSession session = new ConfigurableSession(channel);
        RealmSession realmSession = new RealmSession(session);

        configurator.configure(session, realmSession);

        realmSession.exception(new IOException("Connection reset by peer"));
        realmSession.exception(new IOException("Connexion ré-initialisée par le correspondant"));

        assertFalse(session.isAlive());
        Mockito.verifyNoInteractions(logger);
    }

    @Test
    void exceptionShouldLogIOException() {
        ConfigurableSession session = new ConfigurableSession(channel);
        RealmSession realmSession = new RealmSession(session);

        configurator.configure(session, realmSession);

        realmSession.exception(new IOException("My error"));

        assertFalse(session.isAlive());
        Mockito.verify(logger).error("[{}] IOException : {}", realmSession, "My error");
    }
}

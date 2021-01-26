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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.common.account.banishment;

import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import inet.ipaddr.IPAddressString;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class AbstractBanIpSynchronizerTest extends GameBaseCase {
    private AbstractBanIpSynchronizer synchronizer;
    private BanIpService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        synchronizer = new AbstractBanIpSynchronizer(
            service = container.get(BanIpService.class),
            server::sessions,
            Mockito.mock(Logger.class), Duration.ofMillis(1)
        ) {};

        dataSet.use(Account.class);
    }

    @Override
    @AfterEach
    public void tearDown() throws fr.quatrevieux.araknemu.core.di.ContainerException {
        synchronizer.stopPulling();
        super.tearDown();
    }

    @Test
    void pulling() throws InterruptedException {
        dataSet.push(new BanIp(new IPAddressString("145.32.213.5"), Instant.now(), null, "cause", -1));

        assertFalse(service.isIpBanned(new IPAddressString("145.32.213.5")));
        synchronizer.startPulling();
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.5")));

        dataSet.push(new BanIp(new IPAddressString("145.32.213.6"), Instant.now().plus(10, ChronoUnit.SECONDS), null, "cause", -1));
        Thread.sleep(100);
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.6")));

        synchronizer.stopPulling();
        Thread.sleep(100);
        dataSet.push(new BanIp(new IPAddressString("145.32.213.7"), Instant.now().plus(10, ChronoUnit.SECONDS), null, "cause", -1));
        Thread.sleep(100);
        assertFalse(service.isIpBanned(new IPAddressString("145.32.213.7")));
    }

    @Test
    void listenerShouldKickSessionIfBanned() {
        container.get(ListenerAggregate.class).add(synchronizer.ipBannedListener());

        GameSession session = server.createSession("145.23.14.5");
        assertTrue(session.isAlive());

        service.newRule(new IPAddressString("145.23.14.5")).apply();

        assertEquals(new LoginError(LoginError.BANNED).toString(), ((DummyChannel) session.channel()).getMessages().peek().toString());
        assertFalse(session.isAlive());
    }

    @Test
    void listenerShouldKickSessionIfBannedOnPullingNewRule() throws InterruptedException {
        container.get(ListenerAggregate.class).add(synchronizer.ipBannedListener());
        synchronizer.startPulling();

        GameSession session = server.createSession("145.23.14.5");
        assertTrue(session.isAlive());

        dataSet.push(new BanIp(new IPAddressString("145.23.14.5"), Instant.now().plus(10, ChronoUnit.SECONDS), null, "cause", -1));
        Thread.sleep(100);

        assertEquals(new LoginError(LoginError.BANNED).toString(), ((DummyChannel) session.channel()).getMessages().peek().toString());
        assertFalse(session.isAlive());
    }
}

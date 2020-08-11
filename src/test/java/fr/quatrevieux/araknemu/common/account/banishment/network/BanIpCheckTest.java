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

package fr.quatrevieux.araknemu.common.account.banishment.network;

import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.core.network.session.AbstractDelegatedSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BanIpCheckTest extends GameBaseCase {
    class TestSession extends AbstractDelegatedSession {
        public TestSession(Session session) {
            super(session);
        }
    }

    private BanIpCheck banIpCheck;
    private BanIpService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        banIpCheck = new BanIpCheck(service = container.get(BanIpService.class));
    }

    @Test
    void ipNotBannedShouldDoNothing() {
        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);
        configurator.add(banIpCheck);

        TestSession session = configurator.create(new DummyChannel());

        assertTrue(session.isAlive());
        assertTrue(DummyChannel.class.cast(session.channel()).getMessages().empty());
    }

    @Test
    void ipBannedShouldCloseSession() {
        service.newRule(new IPAddressString("14.25.36.98")).apply();

        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);
        configurator.add(banIpCheck);

        TestSession session = configurator.create(new DummyChannel("14.25.36.98"));

        assertFalse(session.isAlive());
        assertEquals(new LoginError(LoginError.BANNED).toString(), DummyChannel.class.cast(session.channel()).getMessages().peek().toString());
    }
}

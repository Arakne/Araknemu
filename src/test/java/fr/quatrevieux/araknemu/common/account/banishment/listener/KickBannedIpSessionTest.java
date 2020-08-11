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

package fr.quatrevieux.araknemu.common.account.banishment.listener;

import fr.quatrevieux.araknemu.common.account.banishment.BanIpRule;
import fr.quatrevieux.araknemu.common.account.banishment.event.IpBanned;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KickBannedIpSessionTest extends GameBaseCase {
    private KickBannedIpSession listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new KickBannedIpSession(server::sessions);
    }

    @Test
    void onIpBanned() {
        GameSession s1 = server.createSession("14.25.36.1");
        GameSession s2 = server.createSession("14.25.36.2");
        GameSession s3 = server.createSession("41.25.36.1");

        listener.on(new IpBanned(new BanIpRule(new BanIp(new IPAddressString("14.25.36.0/24"), null, null, null, -1), null)));

        assertFalse(s1.isAlive());
        assertFalse(s2.isAlive());
        assertTrue(s3.isAlive());

        assertEquals(new LoginError(LoginError.BANNED).toString(), DummyChannel.class.cast(s1.channel()).getMessages().peek().toString());
        assertEquals(new LoginError(LoginError.BANNED).toString(), DummyChannel.class.cast(s2.channel()).getMessages().peek().toString());
        assertTrue(DummyChannel.class.cast(s3.channel()).getMessages().isEmpty());
    }
}

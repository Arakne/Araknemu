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

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.account.ClientUid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaveClientUidTest extends GameBaseCase {
    @Test
    void success() throws Exception {
        login();
        handlePacket(new ClientUid("my_uid"));

        ConnectionLog log = container.get(ConnectionLogRepository.class).currentSession(session.account().id());
        assertEquals("my_uid", log.clientUid());
    }

    @Test
    void notLogged() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new ClientUid("my_uid")));
    }
}

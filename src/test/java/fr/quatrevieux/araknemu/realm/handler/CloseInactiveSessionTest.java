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

package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.core.network.SessionIdle;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CloseInactiveSessionTest extends RealmBaseCase {
    @Test
    void handle() {
        session.receive(new SessionIdle(Duration.ofMinutes(15)));

        assertFalse(session.isAlive());
        requestStack.assertLast(ServerMessage.inactivity());
    }
}
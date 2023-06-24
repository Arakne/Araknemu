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

package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.realm.host.GameConnector;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HostListTest {
    @Test
    void testToString() {
        GameHost h1 = new GameHost(Mockito.mock(GameConnector.class), 1, 123, "127.0.0.1");
        h1.setCanLog(true);
        h1.setState(GameHost.State.ONLINE);
        GameHost h2 = new GameHost(Mockito.mock(GameConnector.class), 2, 456, "127.0.0.1");
        h2.setState(GameHost.State.SAVING);

        assertEquals("AH1;1;110;1|2;2;110;0", new HostList(Arrays.asList(h1, h2)).toString());
    }
}

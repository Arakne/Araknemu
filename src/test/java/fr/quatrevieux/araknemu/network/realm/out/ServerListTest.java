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

import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerListTest {
    @Test
    void add() {
        ServerList list = new ServerList(5, Arrays.asList(
            new ServerCharacters(1, 3),
            new ServerCharacters(5, 1)
        ));

        assertEquals("AxK5|1,3|5,1", list.toString());
    }

    @Test
    void empty() {
        assertEquals("AxK31536000000", new ServerList(ServerList.ONE_YEAR, Collections.EMPTY_LIST).toString());
    }

}
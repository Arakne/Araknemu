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

package fr.quatrevieux.araknemu.network.game.out.exchange.movement.local;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalExchangeObjectTest {
    @Test
    void generateAddObject() {
        ItemEntry entry = Mockito.mock(ItemEntry.class);
        Mockito.when(entry.id()).thenReturn(5);

        assertEquals("EMKO+5|3", new LocalExchangeObject(entry, 3).toString());
    }

    @Test
    void generateRemoveObject() {
        ItemEntry entry = Mockito.mock(ItemEntry.class);
        Mockito.when(entry.id()).thenReturn(5);

        assertEquals("EMKO-5", new LocalExchangeObject(entry, 0).toString());
    }
}

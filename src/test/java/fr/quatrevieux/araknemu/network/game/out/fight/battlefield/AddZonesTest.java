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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.fight.battlefield;

import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AddZonesTest {
    @Test
    void simpleZone() {
        assertEquals("GDZ+123;4;3", new AddZones(new AddZones.Zone[] { new AddZones.Zone(123, 4, 3) }).toString());
    }

    @Test
    void multipleZones() {
        assertEquals(
        "GDZ+123;4;3|+224;2;1",
            new AddZones(new AddZones.Zone[] {
                new AddZones.Zone(123, 4, 3),
                new AddZones.Zone(224, 2, 1),
            }).toString()
        );
    }

    @Test
    void usingBattlefieldObjectInterface() {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        FightCell cell = Mockito.mock(FightCell.class);

        Mockito.when(cell.id()).thenReturn(123);
        Mockito.when(bo.cell()).thenReturn(cell);
        Mockito.when(bo.size()).thenReturn(4);
        Mockito.when(bo.color()).thenReturn(3);

        assertEquals("GDZ+123;4;3", new AddZones(bo).toString());
    }
}

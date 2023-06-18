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

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DoubleFighterCharacteristicsTest extends FightBaseCase {
    private DoubleFighterCharacteristics characteristics;
    private DoubleFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        Fight fight = createFight();

        fighter = new DoubleFighter(-5, player.fighter());
        fighter.joinFight(fight, fight.map().get(123));

        characteristics = new DoubleFighterCharacteristics(fighter, player.fighter());
    }

    @Test
    void initiative() {
        assertEquals(0, characteristics.initiative());
    }

    @Test
    void discernment() {
        assertEquals(0, characteristics.discernment());
    }

    @Test
    void get() {
        assertEquals(50, characteristics.get(Characteristic.STRENGTH));
        assertEquals(6, characteristics.get(Characteristic.ACTION_POINT));
        assertEquals(3, characteristics.get(Characteristic.MOVEMENT_POINT));
    }

    @Test
    void alter() {
        AtomicReference<FighterCharacteristicChanged> ref = new AtomicReference<>();
        fighter.dispatcher().add(FighterCharacteristicChanged.class, ref::set);

        characteristics.alter(Characteristic.STRENGTH, 10);
        assertEquals(60, characteristics.get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(10, ref.get().value());

        characteristics.alter(Characteristic.STRENGTH, -10);
        assertEquals(50, characteristics.get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(-10, ref.get().value());
    }

    @Test
    void initial() {
        assertSame(player.fighter().characteristics().initial(), characteristics.initial());
        assertEquals(50, characteristics.initial().get(Characteristic.STRENGTH));

        characteristics.alter(Characteristic.STRENGTH, 10);
        assertEquals(50, characteristics.initial().get(Characteristic.STRENGTH));
    }
}

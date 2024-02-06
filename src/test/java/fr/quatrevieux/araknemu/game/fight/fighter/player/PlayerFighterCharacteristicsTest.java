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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlayerFighterCharacteristicsTest extends FightBaseCase {
    private PlayerCharacteristics baseCharacteristics;
    private PlayerFighterCharacteristics fighterCharacteristics;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        baseCharacteristics = player.properties().characteristics();
        fighterCharacteristics = new PlayerFighterCharacteristics(baseCharacteristics, fighter = player.fighter());
    }

    @Test
    void initiative() {
        assertEquals(baseCharacteristics.initiative(), fighterCharacteristics.initiative());
    }

    @Test
    void get() {
        assertEquals(baseCharacteristics.get(Characteristic.ACTION_POINT), fighterCharacteristics.get(Characteristic.ACTION_POINT));
        assertEquals(baseCharacteristics.get(Characteristic.STRENGTH), fighterCharacteristics.get(Characteristic.STRENGTH));
    }

    @Test
    void getWithBuff() {
        fighterCharacteristics.alter(Characteristic.STRENGTH, 10);

        assertEquals(10 + baseCharacteristics.get(Characteristic.STRENGTH), fighterCharacteristics.get(Characteristic.STRENGTH));
    }

    @Test
    void delegates() {
        assertEquals(baseCharacteristics.base(), fighterCharacteristics.base());
        assertEquals(baseCharacteristics.stuff(), fighterCharacteristics.stuff());
        assertEquals(baseCharacteristics.feats(), fighterCharacteristics.feats());
        assertEquals(baseCharacteristics.boostPoints(), fighterCharacteristics.boostPoints());
        assertEquals(baseCharacteristics.pods(), fighterCharacteristics.pods());
    }

    @Test
    void alter() {
        AtomicReference<FighterCharacteristicChanged> ref = new AtomicReference<>();
        fighter.dispatcher().add(FighterCharacteristicChanged.class, ref::set);

        fighterCharacteristics.alter(Characteristic.STRENGTH, 10);
        assertEquals(10, fighterCharacteristics.boost().get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(10, ref.get().value());

        fighterCharacteristics.alter(Characteristic.STRENGTH, -10);
        assertEquals(0, fighterCharacteristics.boost().get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(-10, ref.get().value());
    }

    @Test
    void discernment() {
        assertEquals(100, fighterCharacteristics.discernment());

        fighterCharacteristics.alterDiscernment(50);
        assertEquals(150, fighterCharacteristics.discernment());

        fighterCharacteristics.alterDiscernment(-50);
        assertEquals(100, fighterCharacteristics.discernment());

        fighterCharacteristics.alterDiscernment(-20);
        assertEquals(80, fighterCharacteristics.discernment());

        fighterCharacteristics.alterDiscernment(-100);
        assertEquals(0, fighterCharacteristics.discernment());
    }

    @Test
    void initial() {
        assertSame(baseCharacteristics, fighterCharacteristics.initial());
    }
}

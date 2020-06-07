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

package fr.quatrevieux.araknemu.game.monster;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonsterTest extends GameBaseCase {
    private Monster monster;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        monster = container.get(MonsterService.class).load(34).all().get(2);
    }

    @Test
    void values() {
        assertEquals(34, monster.id());
        assertEquals(1568, monster.gfxId());
        assertEquals(3, monster.gradeNumber());
        assertEquals(8, monster.level());
        assertEquals(Colors.DEFAULT, monster.colors());
        assertEquals(50, monster.life());
        assertEquals(35, monster.initiative());
        assertEquals("AGGRESSIVE", monster.ai());

        assertEquals(5, monster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(3, monster.characteristics().get(Characteristic.MOVEMENT_POINT));

        assertEquals(new Interval(50, 70), monster.reward().kamas());
        assertEquals(58, monster.reward().experience());
    }
}

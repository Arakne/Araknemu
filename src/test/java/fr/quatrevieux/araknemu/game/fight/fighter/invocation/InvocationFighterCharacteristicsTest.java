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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class InvocationFighterCharacteristicsTest extends FightBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        createFight();

        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;
    }

    @Test
    void withLevel1Invoker() {
        InvocationFighterCharacteristics characteristics = new InvocationFighterCharacteristics(
            container.get(MonsterService.class).load(36).get(1),
            Mockito.mock(Fighter.class),
            other.fighter()
        );

        assertEquals(80, characteristics.get(Characteristic.STRENGTH));
        assertEquals(80, characteristics.get(Characteristic.INTELLIGENCE));
        assertEquals(80, characteristics.get(Characteristic.LUCK));
        assertEquals(70, characteristics.get(Characteristic.AGILITY));
        assertEquals(60, characteristics.get(Characteristic.WISDOM));

        System.out.println(player.fighter().level());
    }

    @Test
    void withLevel50Invoker() {
        InvocationFighterCharacteristics characteristics = new InvocationFighterCharacteristics(
            container.get(MonsterService.class).load(36).get(1),
            Mockito.mock(Fighter.class),
            player.fighter()
        );

        assertEquals(120, characteristics.get(Characteristic.STRENGTH));
        assertEquals(120, characteristics.get(Characteristic.INTELLIGENCE));
        assertEquals(120, characteristics.get(Characteristic.LUCK));
        assertEquals(105, characteristics.get(Characteristic.AGILITY));
        assertEquals(60, characteristics.get(Characteristic.WISDOM));
    }

    @Test
    void baseMethods() {
        InvocationFighterCharacteristics characteristics = new InvocationFighterCharacteristics(
            container.get(MonsterService.class).load(36).get(1),
            Mockito.mock(Fighter.class),
            player.fighter()
        );

        assertEquals(0, characteristics.discernment());
        assertEquals(0, characteristics.initiative());
        assertSame(container.get(MonsterService.class).load(36).get(1).characteristics(), characteristics.initial());
    }

    @Test
    void alter() {
        Fighter invoc = Mockito.mock(Fighter.class);

        InvocationFighterCharacteristics characteristics = new InvocationFighterCharacteristics(
            container.get(MonsterService.class).load(36).get(1),
            invoc,
            player.fighter()
        );

        characteristics.alter(Characteristic.STRENGTH, 20);
        Mockito.verify(invoc).dispatch(Mockito.argThat(event -> event instanceof FighterCharacteristicChanged && ((FighterCharacteristicChanged) event).characteristic() == Characteristic.STRENGTH && ((FighterCharacteristicChanged) event).value() == 20));

        assertEquals(140, characteristics.get(Characteristic.STRENGTH));
        assertEquals(120, characteristics.get(Characteristic.INTELLIGENCE));

        characteristics.alter(Characteristic.INTELLIGENCE, -50);
        Mockito.verify(invoc).dispatch(Mockito.argThat(event -> event instanceof FighterCharacteristicChanged && ((FighterCharacteristicChanged) event).characteristic() == Characteristic.INTELLIGENCE && ((FighterCharacteristicChanged) event).value() == -50));

        assertEquals(140, characteristics.get(Characteristic.STRENGTH));
        assertEquals(70, characteristics.get(Characteristic.INTELLIGENCE));
    }
}

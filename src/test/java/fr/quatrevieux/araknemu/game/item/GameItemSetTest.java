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

package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameItemSetTest extends TestCase {
    private GameItemSet itemSet;

    @BeforeEach
    void setUp() {
        itemSet = new GameItemSet(
            new ItemSet(1, "My itemset", new ArrayList<>()),
            Arrays.asList(
                new GameItemSet.Bonus(
                    Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 15, 0, 0, "")),
                    Arrays.asList(new CharacteristicEffect(Effect.ADD_AGILITY, 15, 1, Characteristic.AGILITY)),
                    new ArrayList<>()
                ),
                new GameItemSet.Bonus(
                    Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 30, 0, 0, "")),
                    Arrays.asList(new CharacteristicEffect(Effect.ADD_AGILITY, 30, 1, Characteristic.AGILITY)),
                    new ArrayList<>()
                )
            )
        );
    }

    @Test
    void getters() {
        assertEquals(1, itemSet.id());
        assertEquals("My itemset", itemSet.name());
    }

    @Test
    void bonusWithLowItemNumber() {
        GameItemSet.Bonus bonus = itemSet.bonus(1);

        assertCount(0, bonus.effects());
        assertCount(0, bonus.characteristics());
        assertCount(0, bonus.specials());
    }

    @Test
    void bonus() {
        GameItemSet.Bonus bonus = itemSet.bonus(2);

        assertCount(1, bonus.effects());
        assertCount(1, bonus.characteristics());
        assertCount(0, bonus.specials());

        assertEquals(
            Arrays.asList(new CharacteristicEffect(Effect.ADD_AGILITY, 15, 1, Characteristic.AGILITY)),
            bonus.characteristics()
        );
    }

    @Test
    void bonusWithHighItemNumber() {
        GameItemSet.Bonus bonus = itemSet.bonus(8);

        assertCount(1, bonus.effects());
        assertCount(1, bonus.characteristics());
        assertCount(0, bonus.specials());

        assertEquals(
            Arrays.asList(new CharacteristicEffect(Effect.ADD_AGILITY, 30, 1, Characteristic.AGILITY)),
            bonus.characteristics()
        );
    }
}

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

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecialEffectsTest extends TestCase {
    private SpecialEffects effects;

    @BeforeEach
    void setUp() {
        effects = new SpecialEffects();
    }

    @Test
    void add() {
        effects.add(SpecialEffects.Type.PODS, 10);

        assertEquals(10, effects.get(SpecialEffects.Type.PODS));

        effects.add(SpecialEffects.Type.PODS, 15);

        assertEquals(25, effects.get(SpecialEffects.Type.PODS));
    }

    @Test
    void sub() {
        effects.sub(SpecialEffects.Type.PODS, 10);

        assertEquals(-10, effects.get(SpecialEffects.Type.PODS));

        effects.sub(SpecialEffects.Type.PODS, 15);

        assertEquals(-25, effects.get(SpecialEffects.Type.PODS));
    }

    @Test
    void clear() {
        effects.add(SpecialEffects.Type.PODS, 10);
        effects.clear();

        assertEquals(0, effects.get(SpecialEffects.Type.PODS));
    }
}

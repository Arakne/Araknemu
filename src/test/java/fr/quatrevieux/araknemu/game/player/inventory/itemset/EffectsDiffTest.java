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

package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectsDiffTest extends TestCase {
    @Test
    void diff() {
        EffectsDiff diff = new EffectsDiff(
            Arrays.asList(
                new SpecialEffect(null, Effect.ADD_PODS, new int [] {10, 0, 0}, ""),
                new SpecialEffect(null, Effect.ADD_INITIATIVE, new int [] {10, 0, 0}, "")
            ),
            Arrays.asList(
                new SpecialEffect(null, Effect.ADD_PODS, new int [] {15, 0, 0}, ""),
                new SpecialEffect(null, Effect.ADD_INITIATIVE, new int [] {10, 0, 0}, "")
            )
        );

        assertCount(1, diff.toApply());
        assertEquals(Effect.ADD_PODS, diff.toApply().get(0).effect());
        assertArrayEquals(new int [] {15, 0, 0}, diff.toApply().get(0).arguments());

        assertCount(1, diff.toRelieve());
        assertEquals(Effect.ADD_PODS, diff.toApply().get(0).effect());
        assertArrayEquals(new int [] {10, 0, 0}, diff.toRelieve().get(0).arguments());
    }

    @Test
    void diffFromNothing() {
        EffectsDiff diff = new EffectsDiff(
            new ArrayList<>(),
            Arrays.asList(
                new SpecialEffect(null, Effect.ADD_PODS, new int [] {15, 0, 0}, ""),
                new SpecialEffect(null, Effect.ADD_INITIATIVE, new int [] {10, 0, 0}, "")
            )
        );

        assertCount(2, diff.toApply());
        assertEquals(Effect.ADD_PODS, diff.toApply().get(0).effect());
        assertArrayEquals(new int [] {15, 0, 0}, diff.toApply().get(0).arguments());
        assertEquals(Effect.ADD_INITIATIVE, diff.toApply().get(1).effect());
        assertArrayEquals(new int [] {10, 0, 0}, diff.toApply().get(1).arguments());

        assertCount(0, diff.toRelieve());
    }

    @Test
    void diffToNothing() {
        EffectsDiff diff = new EffectsDiff(
            Arrays.asList(
                new SpecialEffect(null, Effect.ADD_PODS, new int [] {15, 0, 0}, ""),
                new SpecialEffect(null, Effect.ADD_INITIATIVE, new int [] {10, 0, 0}, "")
            ),
            new ArrayList<>()
        );

        assertCount(0, diff.toApply());

        assertCount(2, diff.toRelieve());
        assertArrayEquals(new int [] {15, 0, 0}, diff.toRelieve().get(0).arguments());
        assertEquals(Effect.ADD_PODS, diff.toRelieve().get(0).effect());
        assertEquals(Effect.ADD_INITIATIVE, diff.toRelieve().get(1).effect());
        assertArrayEquals(new int [] {10, 0, 0}, diff.toRelieve().get(1).arguments());
    }
}

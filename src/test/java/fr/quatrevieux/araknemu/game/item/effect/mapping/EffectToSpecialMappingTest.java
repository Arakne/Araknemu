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

package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.AddSpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.NullEffectHandler;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectToSpecialMappingTest extends GameBaseCase {
    private EffectToSpecialMapping mapping;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        mapping = new EffectToSpecialMapping();
    }

    @Test
    void create() {
        assertEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new EffectToSpecialMapping().create(new ItemTemplateEffectEntry(Effect.NULL1, 1, 2, 3, "a"), false)
        );
    }

    @Test
    void createFromRegisteredHandler() {
        assertEquals(
            new SpecialEffect(new AddSpecialEffect(SpecialEffects.Type.PODS), Effect.ADD_PODS, new int[] {200, 0, 0}, "0d0+200"),
            new EffectToSpecialMapping().create(new ItemTemplateEffectEntry(Effect.ADD_PODS, 0, 200, 0, ""), true)
        );
    }

    @Test
    void createFromListMaximize() {
        List<SpecialEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.SUB_INITIATIVE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.SPELL_ADD_BASE_DAMAGE, 10, 0, 384, "")
            ),
            true
        );

        assertCount(3, effects);
        assertEquals(Effect.ADD_PODS, effects.get(0).effect());
        assertEquals(100, effects.get(0).arguments()[0]);
        assertEquals(Effect.SUB_INITIATIVE, effects.get(1).effect());
        assertEquals(10, effects.get(1).arguments()[0]);
        assertEquals(Effect.SPELL_ADD_BASE_DAMAGE, effects.get(2).effect());
        assertArrayEquals(new int [] {10, 0, 384}, effects.get(2).arguments());
    }

    @Test
    void createFromListRandom() {
        List<SpecialEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.SUB_INITIATIVE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.SPELL_ADD_BASE_DAMAGE, 10, 0, 384, "")
            )
        );

        assertCount(3, effects);
        assertEquals(Effect.ADD_PODS, effects.get(0).effect());
        assertBetween(10, 100, effects.get(0).arguments()[0]);
        assertEquals(Effect.SUB_INITIATIVE, effects.get(1).effect());
        assertBetween(10, 100, effects.get(1).arguments()[0]);
        assertEquals(Effect.SPELL_ADD_BASE_DAMAGE, effects.get(2).effect());
        assertArrayEquals(new int [] {10, 0, 384}, effects.get(2).arguments());
    }

    @Test
    void createFromListFixed() {
        List<SpecialEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 25, 0, 0, ""),
                new ItemTemplateEffectEntry(Effect.SUB_INITIATIVE, 32, 0, 0, ""),
                new ItemTemplateEffectEntry(Effect.SPELL_ADD_BASE_DAMAGE, 10, 0, 384, "")
            )
        );

        assertCount(3, effects);
        assertEquals(Effect.ADD_PODS, effects.get(0).effect());
        assertEquals(25, effects.get(0).arguments()[0]);
        assertEquals(Effect.SUB_INITIATIVE, effects.get(1).effect());
        assertEquals(32, effects.get(1).arguments()[0]);
        assertEquals(Effect.SPELL_ADD_BASE_DAMAGE, effects.get(2).effect());
        assertArrayEquals(new int [] {10, 0, 384}, effects.get(2).arguments());
    }
}

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

package fr.quatrevieux.araknemu.game.item.effect;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.special.AddSpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.NullEffectHandler;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SpecialEffectTest extends GameBaseCase {
    @Test
    void getters() {
        SpecialEffect effect = new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a");

        assertEquals(Effect.NULL1, effect.effect());
        assertArrayEquals(new int[] {1, 2, 3}, effect.arguments());
        assertEquals("a", effect.text());
    }

    @Test
    void equalsSameInstance() {
        SpecialEffect effect = new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a");

        assertEquals(effect, effect);
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a")
        );
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"), new Object());
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL2, new int[] {1, 2, 3}, "a")
        );
    }

    @Test
    void equalsBadArgument() {
        assertNotEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 2, 3}, "a")
        );
    }

    @Test
    void equalsBadText() {
        assertNotEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "b")
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a").hashCode(),
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a").hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a").hashCode(),
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL2, new int[] {1, 2, 3}, "a").hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "SpecialEffect{NULL2:[1, 2, 3], 'a'}",
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL2, new int[] {1, 2, 3}, "a").toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry template = new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a").toTemplate();

        assertEquals(Effect.NULL1, template.effect());
        assertEquals(1, template.min());
        assertEquals(2, template.max());
        assertEquals(3, template.special());
        assertEquals("a", template.text());
    }

    @Test
    void apply() throws SQLException, ContainerException {
        SpecialEffect effect = new SpecialEffect(
            new AddSpecialEffect(SpecialEffects.Type.PODS),
            Effect.ADD_PODS,
            new int[] {150, 0, 0}, ""
        );

        effect.apply(gamePlayer(true));

        assertEquals(150, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.PODS));
    }

    @Test
    void relieve() throws SQLException, ContainerException {
        SpecialEffect effect = new SpecialEffect(
            new AddSpecialEffect(SpecialEffects.Type.PODS),
            Effect.ADD_PODS,
            new int[] {150, 0, 0}, ""
        );

        effect.apply(gamePlayer(true));
        effect.relieve(gamePlayer());

        assertEquals(0, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.PODS));
    }
}

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
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.use.AddCharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.AddLifeEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.FireworkEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.NullEffectHandler;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseEffectTest extends GameBaseCase {
    @Test
    void getters() {
        UseEffect effect = new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3});

        assertEquals(Effect.NULL1, effect.effect());
        assertArrayEquals(new int[] {1, 2, 3}, effect.arguments());
    }

    @Test
    void equalsSameInstance() {
        UseEffect effect = new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3});

        assertEquals(effect, effect);
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}),
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3})
        );
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}), new Object());
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}),
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL2, new int[] {1, 2, 3})
        );
    }

    @Test
    void equalsBadArgument() {
        assertNotEquals(
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}),
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 2, 3})
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}).hashCode(),
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}).hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}).hashCode(),
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL2, new int[] {1, 2, 3}).hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "UseEffect{NULL2:[1, 2, 3]}",
            new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL2, new int[] {1, 2, 3}).toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry template = new UseEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}).toTemplate();

        assertEquals(Effect.NULL1, template.effect());
        assertEquals(1, template.min());
        assertEquals(2, template.max());
        assertEquals(3, template.special());
        assertEquals("", template.text());
    }

    @Test
    void apply() throws SQLException, ContainerException {
        UseEffect effect = new UseEffect(new AddCharacteristicEffect(Characteristic.AGILITY), Effect.NULL1, new int[] {1, 0, 0});

        effect.apply(explorationPlayer());

        assertEquals(1, explorationPlayer().properties().characteristics().base().get(Characteristic.AGILITY));
    }

    @Test
    void applyToTarget() throws SQLException, ContainerException {
        UseEffect effect = new UseEffect(new FireworkEffect(), Effect.NULL1, new int[] {1, 0, 2900});

        effect.applyToTarget(explorationPlayer(), null, explorationPlayer().map().get(150));

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.FIREWORK, explorationPlayer().id(), "150,2900,11,8,1")
        );
    }

    @Test
    void check() throws SQLException, ContainerException {
        UseEffect effect = new UseEffect(new FireworkEffect(), Effect.NULL1, new int[] {1, 0, 2900});

        assertTrue(effect.checkTarget(null, null, explorationPlayer().map().get(150)));
        assertFalse(effect.check(explorationPlayer()));
        assertFalse(effect.checkFighter(container.get(FighterFactory.class).create(gamePlayer())));
    }

    @Test
    void applyToFighter() throws SQLException, ContainerException {
        gamePlayer().properties().life().set(10);
        PlayerFighter fighter = container.get(FighterFactory.class).create(gamePlayer());

        UseEffect effect = new UseEffect(new AddLifeEffect(), Effect.NULL1, new int[] {10, 0, 0});

        effect.applyToFighter(fighter);

        assertEquals(20, fighter.life().current());
    }
}

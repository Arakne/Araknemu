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

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.special.SpecialEffectHandler;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.Arrays;

/**
 * Special effect
 */
public final class SpecialEffect implements ItemEffect {
    private final SpecialEffectHandler handler;
    private final Effect effect;
    private final @NonNegative int @ArrayLen(3)[] arguments;
    private final String text;

    public SpecialEffect(SpecialEffectHandler handler, Effect effect, @NonNegative int @ArrayLen(3)[] arguments, String text) {
        this.handler = handler;
        this.effect = effect;
        this.arguments = arguments;
        this.text = text;
    }

    @Override
    public Effect effect() {
        return effect;
    }

    @Override
    public ItemTemplateEffectEntry toTemplate() {
        return new ItemTemplateEffectEntry(
            effect,
            arguments[0],
            arguments[1],
            arguments[2],
            text
        );
    }

    public @NonNegative int @ArrayLen(3)[] arguments() {
        return arguments;
    }

    public String text() {
        return text;
    }

    /**
     * Apply the effect to the player
     */
    public void apply(GamePlayer player) {
        handler.apply(this, player);
    }

    /**
     * Remove the effect from the player
     */
    public void relieve(GamePlayer player) {
        handler.relieve(this, player);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SpecialEffect that = (SpecialEffect) o;

        return effect == that.effect
            && Arrays.equals(arguments, that.arguments)
            && text.equals(that.text)
        ;
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + Arrays.hashCode(arguments);
        result = 31 * result + text.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "SpecialEffect{" + effect + ":" + Arrays.toString(arguments) + ", '" + text + "'}";
    }
}

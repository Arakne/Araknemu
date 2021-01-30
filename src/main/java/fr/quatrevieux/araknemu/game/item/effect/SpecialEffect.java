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

import java.util.Arrays;

/**
 * Special effect
 */
final public class SpecialEffect implements ItemEffect {
    final private SpecialEffectHandler handler;
    final private Effect effect;
    final private int[] arguments;
    final private String text;

    public SpecialEffect(SpecialEffectHandler handler, Effect effect, int[] arguments, String text) {
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

    public int[] arguments() {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
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

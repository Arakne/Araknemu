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
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.use.UseEffectHandler;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.Arrays;

/**
 * Effect for usable items
 */
public final class UseEffect implements ItemEffect {
    private final UseEffectHandler handler;
    private final Effect effect;
    private final @NonNegative int @ArrayLen(3) [] arguments;

    public UseEffect(UseEffectHandler handler, Effect effect, @NonNegative int @ArrayLen(3)[] arguments) {
        this.handler = handler;
        this.effect = effect;
        this.arguments = arguments;
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
            ""
        );
    }

    public @NonNegative int @ArrayLen(3)[] arguments() {
        return arguments;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UseEffect useEffect = (UseEffect) o;

        return effect == useEffect.effect
            && Arrays.equals(arguments, useEffect.arguments)
        ;
    }

    /**
     * Check if the player can use the effect
     *
     * @see UseEffectHandler#check(UseEffect, ExplorationPlayer)
     */
    public boolean check(ExplorationPlayer caster) {
        return handler.check(this, caster);
    }

    /**
     * Check if the player can use the effect to the target
     *
     * @see UseEffectHandler#checkTarget(UseEffect, ExplorationPlayer, ExplorationPlayer, ExplorationMapCell)
     */
    public boolean checkTarget(ExplorationPlayer caster, @Nullable ExplorationPlayer target, @Nullable ExplorationMapCell cell) {
        return handler.checkTarget(this, caster, target, cell);
    }

    /**
     * Check if the fighter can use the effect
     *
     * @see UseEffectHandler#checkFighter(UseEffect, PlayerFighter)
     */
    public boolean checkFighter(PlayerFighter fighter) {
        return handler.checkFighter(this, fighter);
    }

    /**
     * Apply the effect to the player
     *
     * @see UseEffectHandler#apply(UseEffect, ExplorationPlayer)
     */
    public void apply(ExplorationPlayer caster) {
        handler.apply(this, caster);
    }

    /**
     * Apply the effect to the target
     *
     * @see UseEffectHandler#applyToTarget(UseEffect, ExplorationPlayer, ExplorationPlayer, ExplorationMapCell)
     */
    public void applyToTarget(ExplorationPlayer caster, @Nullable ExplorationPlayer target, @Nullable ExplorationMapCell cell) {
        handler.applyToTarget(this, caster, target, cell);
    }

    /**
     * Apply the effect to the fighter
     *
     * @see UseEffectHandler#applyToFighter(UseEffect, PlayerFighter)
     */
    public void applyToFighter(PlayerFighter fighter) {
        handler.applyToFighter(this, fighter);
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + Arrays.hashCode(arguments);

        return result;
    }

    @Override
    public String toString() {
        return "UseEffect{" + effect + ":" + Arrays.toString(arguments) + '}';
    }
}

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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.environment.LaunchFirework;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Effect for fireworks
 */
public final class FireworkEffect implements UseEffectHandler {
    private final RandomUtil random = new RandomUtil();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        throw new UnsupportedOperationException("Cannot apply firework on self");
    }

    @Override
    @SuppressWarnings("argument") // @todo to remove when cell will be an object
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @GTENegativeOne int cell) {
        caster.interactions().push(
            new LaunchFirework(
                caster,
                cell,
                effect.arguments()[2],
                random.rand(effect.arguments())
            )
        );
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return false;
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @GTENegativeOne int cell) {
        return cell != -1; // @todo check if cell exists
    }
}

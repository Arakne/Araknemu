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
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.util.NullnessUtil;

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
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @Nullable ExplorationMapCell cell) {
        caster.interactions().push(
            new LaunchFirework(
                caster,
                NullnessUtil.castNonNull(cell).id(),
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
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @Nullable ExplorationMapCell cell) {
        return cell != null;
    }
}

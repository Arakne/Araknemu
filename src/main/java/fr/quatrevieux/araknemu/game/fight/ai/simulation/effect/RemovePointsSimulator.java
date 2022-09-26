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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Simulation points removals (AP or MP)
 *
 * Note: an approximation is used instead of actual algorithm for compute removed points
 */
public final class RemovePointsSimulator implements EffectSimulator {
    private final Characteristic pointCharacteristic;
    private final Characteristic resistanceCharacteristic;
    private final @Positive int multiplier;

    /**
     * @param pointCharacteristic Removed point characteristic (i.e. Characteristic.ACTION_POINT or Characteristic.MOVEMENT_POINT)
     * @param resistanceCharacteristic Characteristic use for dodge point removal
     * @param multiplier Boost multiplier. Must be a positive integer
     */
    public RemovePointsSimulator(Characteristic pointCharacteristic, Characteristic resistanceCharacteristic, @Positive int multiplier) {
        this.pointCharacteristic = pointCharacteristic;
        this.resistanceCharacteristic = resistanceCharacteristic;
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope<? extends FighterData>.EffectScope effect) {
        final int casterChance = Math.max(simulation.caster().characteristics().get(Characteristic.WISDOM) / 10, 1);

        int duration = effect.effect().duration();

        if (duration == -1 || duration > 10) {
            duration = 10;
        }

        if (duration == 0) {
            duration = 1;
        }

        for (FighterData target : effect.targets()) {
            // @todo actual turn AP is target is current fighter
            final int currentPoints = target.characteristics().get(pointCharacteristic);

            if (currentPoints <= 0) {
                continue;
            }

            final int targetResistance = Math.max(target.characteristics().get(resistanceCharacteristic), 1);
            final double percent = Math.min(0.5 * casterChance / targetResistance, 1.0);
            final double value = multiplier * EffectValue.create(effect.effect(), simulation.caster(), target).interval().average();
            final double maxValue = currentPoints * multiplier;

            simulation.addBoost(
                - Math.min(maxValue, percent * value) * duration,
                target
            );
        }
    }
}

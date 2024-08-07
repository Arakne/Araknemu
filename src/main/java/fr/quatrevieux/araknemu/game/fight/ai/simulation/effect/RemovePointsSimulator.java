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
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
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
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final int casterChance = Math.max(simulation.caster().characteristics().get(Characteristic.WISDOM) / 10, 1);
        final int duration = Formula.capedDuration(effect.effect().duration());

        for (FighterData target : effect.targets()) {
            // @todo actual turn AP is target is current fighter
            final int currentPoints = target.characteristics().get(pointCharacteristic);

            if (currentPoints <= 0) {
                continue;
            }

            final int targetResistance = Math.max(target.characteristics().get(resistanceCharacteristic), 1);
            final double percent = Math.min(0.5 * casterChance / targetResistance, 1.0);
            final double value = multiplier * new EffectValue(effect.effect()).interval().average();
            final double maxValue = currentPoints * multiplier;

            simulation.addBoost(
                - Math.min(maxValue, percent * value) * duration,
                target
            );
        }
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        final int min = effect.min();
        final int max = Math.max(effect.max(), min);
        final int value = (min + max) * multiplier / 2;

        score.addDebuff(value);
    }
}

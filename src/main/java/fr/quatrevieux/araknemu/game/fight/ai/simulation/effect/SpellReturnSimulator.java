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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Simulate spell return effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.SpellReturnHandler
 */
public final class SpellReturnSimulator implements EffectSimulator, BuffEffectSimulator {
    private final int multiplier;

    public SpellReturnSimulator(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final double value = score(effect.effect());

        for (FighterData target : effect.targets()) {
            simulation.addBoost(value, target);
        }
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        score.addBoost(Asserter.castNonNegative((int) score(effect)));
    }

    @Override
    public Damage onReduceableDamage(CastSimulation simulation, Buff buff, FighterData target, Damage damage) {
        final SpellEffect effect = buff.effect();
        final int level = Math.max(effect.min(), effect.max());
        final double percent = effect.special();

        // Ignore the effect if it's too random, or doesn't apply to the current cast
        if (percent < 90 || level < simulation.spell().level()) {
            return damage;
        }

        final int currentDamage = damage.value();

        if (currentDamage <= 0) {
            return damage;
        }

        // Simulate return effect by reducing all damage, and mark it as reflected
        // This is not the actual behavior (i.e. change the target), but it's enough for simulation
        return damage
            .reflect(currentDamage)
            .reduce(currentDamage)
        ;
    }

    private double score(SpellEffect effect) {
        final int level = Math.max(effect.min(), effect.max());
        final double percent = effect.special() / 100d;
        final int duration = Formula.capedDuration(effect.duration());

        return level * percent * multiplier * duration;
    }
}

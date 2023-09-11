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
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;

/**
 * Simulate bonus for switch position on attack effect
 *
 * The bonus will be the life of the caster multiplied by the duration of the effect
 * To favorite low HP target, the bonus is multiplied by the ratio of the caster life on target life
 *
 * A malus equivalent to the life of the caster multiplied by the duration of the effect will be applied to the caster
 * only if it is not an invocation
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.SwitchPositionOnAttackHandler
 */
public final class SwitchPositionOnAttackSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final FighterData caster = simulation.caster();
        final int currentLife = caster.life().current();
        final int duration = effect.effect().duration() == -1
            ? 10
            : Math.min(effect.effect().duration(), 10)
        ;

        for (FighterData target : effect.targets()) {
            if (target.equals(caster)) {
                continue;
            }

            // Add a bonus equivalent of the life of the caster
            // modulated by the ratio of the caster on target life
            // so, target with lower life will have a higher bonus
            simulation.addBoost(
                (double) (currentLife * currentLife * duration) / target.life().current(),
                target
            );
        }

        // Ignore malus if the caster is an invocation
        if (!caster.invoked()) {
            simulation.addBoost(-currentLife * duration, caster);
        }
    }
}

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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.util.Predicates;

/**
 * AI between {@link Runaway} and {@link Aggressive}
 *
 * It will choose the best move for attack,
 * and move near enemies if out of range, or move away otherwise
 *
 * This AI is designed for monsters with long range attack spells
 */
public final class Tactical extends AbstractAiBuilderFactory {
    private final Simulator simulator;

    public Tactical(Simulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void configure(GeneratorBuilder builder) {
        builder
            .boostSelf(simulator)
            .attackFromBestCell(simulator)
            .when(Predicates.hasLessThanPercentLife(50), cond -> cond.success(gb -> gb.heal(simulator)))
            .debuff(simulator)
            .boostAllies(simulator)
            .heal(simulator)
            .when(Predicates.hasEnemyInRange(), cond -> cond
                .success(GeneratorBuilder::moveFarEnemies)
                .otherwise(GeneratorBuilder::moveOrTeleportNearEnemy)
            )
        ;
    }
}

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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.ai.memory.MemoryKey;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;

/**
 * AI for monster which can randomly attack allies or enemies
 * Used by Chaferfu
 */
public final class Lunatic extends AbstractAiBuilderFactory {
    private static final MemoryKey<Boolean> TARGET_ALLIES = new TargetAlliesMemory();
    private final Simulator simulator;

    public Lunatic(Simulator simulator) {
        this.simulator = simulator;
    }

    @Override
    protected void configure(GeneratorBuilder builder) {
        builder.when(ai -> Boolean.TRUE.equals(ai.get(TARGET_ALLIES)), cb -> cb
            .success(b -> b
                .castFromBestCell(simulator, simulation -> castScore(simulation, true))
                .moveNearAllies()
            )
            .otherwise(b -> b
                .castFromBestCell(simulator, simulation -> castScore(simulation, false))
                .moveNearEnemy()
            )
        );
    }

    private static double castScore(CastSimulation simulation, boolean targetAllies) {
        final double alliesScore = simulation.alliesBoost() + simulation.alliesLife() - 100 * simulation.killedAllies();
        final double enemiesScore = simulation.enemiesBoost() + simulation.enemiesLife() - 100 * simulation.killedEnemies();
        final double selfScore = simulation.selfBoost() + simulation.selfLife() - 100 * simulation.suicideProbability() + simulation.invocation();

        final double baseScore = targetAllies
            ? -alliesScore + enemiesScore + selfScore
            : alliesScore - enemiesScore + selfScore
        ;

        return baseScore / simulation.actionPointsCost();
    }

    private static final class TargetAlliesMemory implements MemoryKey<Boolean> {
        private static final RandomUtil RANDOM = RandomUtil.createShared();

        @Override
        public Boolean defaultValue() {
            return RANDOM.nextBoolean();
        }

        @Override
        public Boolean refresh(Boolean value) {
            return RANDOM.nextBoolean();
        }
    }
}

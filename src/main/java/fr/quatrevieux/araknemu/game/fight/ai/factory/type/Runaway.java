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

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.CastSpell;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;

/**
 * AI for run away monsters (like Tofu)
 *
 * This AI use the smallest MP quantity for attack, and flees farthest from enemies
 * If the monster cannot attack (or cast a spell), it will move near the nearest enemy
 */
public final class Runaway extends AbstractAiBuilderFactory {
    private final Simulator simulator;

    public Runaway(Simulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void configure(GeneratorBuilder builder) {
        builder
            .boostSelf(simulator)
            .attack(simulator)
            .invoke(simulator)
            .attackFromNearestCell(simulator)
            .boostAllies(simulator)
            .when(ai -> ai.get(CastSpell.LAST_CAST) != null, cb -> cb
                .success(GeneratorBuilder::moveFarEnemies)
                .otherwise(GeneratorBuilder::moveNearEnemy)
            )
        ;
    }
}

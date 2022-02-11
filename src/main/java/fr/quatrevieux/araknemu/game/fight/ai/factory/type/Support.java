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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * AI for support allies on fight
 * This AI will boost allies in priority, or attack otherwise, and move near allies
 *
 * If the fighter is alone (i.e. without any allies), use the AI passed on constructor (or {@link Tactical} by default)
 */
public final class Support extends AbstractAiBuilderFactory {
    private final Simulator simulator;
    private final AbstractAiBuilderFactory aloneAi;

    public Support(Simulator simulator) {
        this(simulator, new Tactical(simulator));
    }

    public Support(Simulator simulator, AbstractAiBuilderFactory aloneAi) {
        this.simulator = simulator;
        this.aloneAi = aloneAi;
    }

    @Override
    public void configure(GeneratorBuilder builder, Fighter fighter) {
        builder.when(Predicates.hasAllies(), cb -> cb
            .success(sb -> sb
                .boostAllies(simulator)
                .attack(simulator)
                // @todo move to boost
                .moveNearAllies()
            )
            .otherwise(sb -> aloneAi.configure(sb, fighter))
        );
    }
}

import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder
import fr.quatrevieux.araknemu.game.fight.ai.action.util.CastSpell
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Movement
import fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator

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

/**
 * Example of a random AI
 *
 * To create a new AI type you have to extends AbstractAiBuilderFactory and override one of the configure method
 * The AI name will be the class name in upper case. If you want to change the name, you can override the name() method
 *
 * Note: be aware of the maximum length of the name. By default the maximum length is 12 characters.
 */
class Random extends AbstractAiBuilderFactory {
    final Simulator simulator
    final java.util.Random random

    // The scripting API will inject dynamically all the required dependencies
    // If one of the dependencies is not declared in the container, it will be created automatically
    Random(Simulator simulator) {
        this.simulator = simulator
        this.random = new java.util.Random()
    }


    // Override this method to configure the AI
    // Note: you can also override configure(GeneratorBuilder builder, PlayableFighter fighter) instead of this one to have access to the fighter
    @Override
    protected void configure(GeneratorBuilder builder) {
        // Now you can add actions to the AI pipeline
        // The pipeline defines the priority of the actions
        // If the first action can be executed, it will be executed until it can't
        // And then the second action will be executed, and so on
        builder
            .add(new Movement({ Math.random() }, { true }))
            .add(new CastSpell(simulator, new CastSpell.SimulationSelector() {
                @Override
                boolean valid(CastSimulation simulation) {
                    random.nextBoolean()
                }

                @Override
                double score(CastSimulation simulation) {
                    Math.random()
                }
            }))
    }
}

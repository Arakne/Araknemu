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

package fr.quatrevieux.araknemu.game.fight.ai.action.logic;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Aggregation of AI action generator
 *
 * This class can be seen as a pipeline of action generators :
 * - If a generator cannot generate an action, the next one is called
 * - If all generators fail, the turn is stopped
 * - When an action is successfully generated, it will be executed,
 *   and the "pipeline" is reset after the action termination
 */
public final class GeneratorAggregate implements ActionGenerator {
    private final ActionGenerator[] actions;

    public GeneratorAggregate(ActionGenerator[] actions) {
        this.actions = actions;
    }

    @Override
    public void initialize(AI ai) {
        for (ActionGenerator generator : actions) {
            generator.initialize(ai);
        }
    }

    @Override
    public Optional<Action> generate(AI ai) {
        for (ActionGenerator generator : actions) {
            final Optional<Action> generated = generator.generate(ai);

            if (generated.isPresent()) {
                return generated;
            }
        }

        return Optional.empty();
    }
}

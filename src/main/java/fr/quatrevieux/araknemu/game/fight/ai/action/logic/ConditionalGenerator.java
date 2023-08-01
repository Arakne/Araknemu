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
import fr.quatrevieux.araknemu.game.fight.ai.action.AiActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * An "if then else" action generator
 *
 * Will call the "success" generator (2nd constructor parameter) if the condition is true,
 * or call the "otherwise" generator (3rd constructor parameter) on false
 *
 * @see ConditionalGenerator For build a conditional generator
 */
public final class ConditionalGenerator implements ActionGenerator {
    private final Predicate<AI> condition;
    private final ActionGenerator success;
    private final ActionGenerator otherwise;

    public ConditionalGenerator(Predicate<AI> condition, ActionGenerator success, ActionGenerator otherwise) {
        this.condition = condition;
        this.success = success;
        this.otherwise = otherwise;
    }

    @Override
    public void initialize(AI ai) {
        success.initialize(ai);
        otherwise.initialize(ai);
    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        return condition.test(ai) ? success.generate(ai, actions) : otherwise.generate(ai, actions);
    }
}

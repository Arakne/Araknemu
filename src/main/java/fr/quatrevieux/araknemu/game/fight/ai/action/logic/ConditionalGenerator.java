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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
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
public final class ConditionalGenerator<F extends ActiveFighter> implements ActionGenerator<F> {
    private final Predicate<AI<F>> condition;
    private final ActionGenerator<F> success;
    private final ActionGenerator<F> otherwise;

    public ConditionalGenerator(Predicate<AI<F>> condition, ActionGenerator<F> success, ActionGenerator<F> otherwise) {
        this.condition = condition;
        this.success = success;
        this.otherwise = otherwise;
    }

    @Override
    public void initialize(AI<F> ai) {
        success.initialize(ai);
        otherwise.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        return condition.test(ai) ? success.generate(ai, actions) : otherwise.generate(ai, actions);
    }
}

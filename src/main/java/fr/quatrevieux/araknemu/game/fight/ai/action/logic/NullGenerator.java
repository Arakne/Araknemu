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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;

import java.util.Optional;

/**
 * A No-op generator
 */
public final class NullGenerator<F extends ActiveFighter> implements ActionGenerator<F> {
    public static final NullGenerator<ActiveFighter> INSTANCE = new NullGenerator<>();

    @Override
    public void initialize(AI<F> ai) {
        // No-op
    }

    @Override
    public Optional<Action> generate(AI<F> ai, ActionsFactory<F> actions) {
        return Optional.empty();
    }

    /**
     * Get the instance of the null generator
     */
    @SuppressWarnings("unchecked")
    public static <F extends ActiveFighter> NullGenerator<F> get() {
        return (NullGenerator<F>) INSTANCE;
    }
}

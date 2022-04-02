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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.common.value.qual.MinLen;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Handle fight states flow
 */
public final class StatesFlow {
    private final FightState @MinLen(1) [] states;
    private @IndexFor("states") int current = 0;

    public StatesFlow(FightState @MinLen(1)... states) {
        this.states = states;
    }

    /**
     * Get the current fight state
     */
    @Pure
    public FightState current() {
        return states[current];
    }

    /**
     * Start the next state
     */
    @SuppressWarnings({"array.access.unsafe.high", "unary.increment"}) // Let java fails if current is too high
    public void next(Fight fight) {
        states[++current].start(fight);
    }
}

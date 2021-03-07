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

package fr.quatrevieux.araknemu.game.fight.ending;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;

import java.util.List;

/**
 * Result for fight end
 */
public final class EndFightResults {
    private final Fight fight;
    private final List<Fighter> winners;
    private final List<Fighter> loosers;

    public EndFightResults(Fight fight, List<Fighter> winners, List<Fighter> loosers) {
        this.fight = fight;
        this.winners = winners;
        this.loosers = loosers;
    }

    /**
     * Get the fight
     */
    public Fight fight() {
        return fight;
    }

    /**
     * Get the winner team
     */
    public List<Fighter> winners() {
        return winners;
    }

    /**
     * Get the loosers
     */
    public List<Fighter> loosers() {
        return loosers;
    }

    /**
     * Apply the operation to all looser fighters
     *
     * @param <O> The operation type
     *
     * @return The given operation
     *
     * @see Fighter#apply(FighterOperation)
     */
    public <O extends FighterOperation> O applyToLoosers(O operation) {
        loosers.forEach(fighter -> fighter.apply(operation));

        return operation;
    }

    /**
     * Apply the operation to all winner fighters
     *
     * @param <O> The operation type
     *
     * @return The given operation
     *
     * @see Fighter#apply(FighterOperation)
     */
    public <O extends FighterOperation> O applyToWinners(O operation) {
        winners.forEach(fighter -> fighter.apply(operation));

        return operation;
    }
}

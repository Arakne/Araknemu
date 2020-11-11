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

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnListChanged;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle fight turns
 */
final public class FightTurnList {
    final private Fight fight;

    private List<Fighter> fighters;
    private int current;
    private FightTurn turn;
    private AtomicBoolean active = new AtomicBoolean(false);

    public FightTurnList(Fight fight) {
        this.fight = fight;
    }

    /**
     * Initialise the fighters order
     */
    public void init(FighterOrderStrategy orderStrategy) {
        if (fighters != null) {
            throw new IllegalStateException("FightTurnList is already initialised");
        }

        fighters = orderStrategy.compute(fight.teams());
    }

    /**
     * Get all fighters ordered by their turn order
     */
    public List<Fighter> fighters() {
        return fighters;
    }

    /**
     * Remove a fighter from turn list
     *
     * @param fighter Fighter to remove
     */
    public void remove(Fighter fighter) {
        fighters.remove(fighter); // @fixme change current (get index + if before decrement current)

        fight.dispatch(new TurnListChanged(this));
    }

    /**
     * Get the current turn
     */
    public Optional<FightTurn> current() {
        return Optional.ofNullable(turn);
    }

    /**
     * Get the current turn fighter
     */
    public Fighter currentFighter() {
        return fighters.get(current);
    }

    /**
     * Start the turn system
     */
    public void start() {
        if (active.getAndSet(true)) {
            throw new IllegalStateException("TurnList already started");
        }

        current = -1;

        next();
    }

    /**
     * Stop the turn system
     */
    public void stop() {
        if (!active.getAndSet(false)) {
            return;
        }

        if (turn != null) {
            turn.stop();
            turn = null;
        }
    }

    /**
     * Stop the current turn and start the next
     *
     * @todo test start with return false
     */
    void next() {
        turn = null;
        fight.dispatch(new NextTurnInitiated());

        while (active.get()) {
            if (++current >= fighters.size()) {
                current = 0;
            }

            if (fighters.get(current).dead()) {
                continue;
            }

            turn = new FightTurn(fighters.get(current), fight, fight.type().turnDuration());

            if (turn.start()) {
                break;
            }
        }
    }
}

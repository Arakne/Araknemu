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
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnListChanged;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle fight turns
 */
public final class FightTurnList {
    private final Fight fight;

    private final List<Fighter> fighters;
    private Fighter current;
    private @Nullable FightTurn turn;
    private int index;
    private final AtomicBoolean active = new AtomicBoolean(false);

    public FightTurnList(Fight fight, FighterOrderStrategy orderStrategy) {
        this.fight = fight;
        this.fighters = orderStrategy.compute(fight.teams());

        if (fighters.isEmpty()) {
            throw new IllegalStateException("Cannot initialise turn list without fighters");
        }

        current = fighters.get(0); // Always init the first fighter
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
     *
     * @see TurnListChanged Event triggered after the list is updated
     */
    public void remove(Fighter fighter) {
        final int index = fighters.indexOf(fighter);

        if (index == -1) {
            return;
        }

        fighters.remove(index);

        // The removed fighter is the current fighter or before on the list
        // so removing it will shift the list to the left relatively to the cursor (current)
        // which cause that the next fighter will be skipped
        // See: https://github.com/Arakne/Araknemu/issues/127
        if (index <= this.index) {
            // If current is negative, move cursor to the end
            if (--this.index < 0) {
                this.index += fighters.size();
            }
        }

        fight.dispatch(new TurnListChanged(this));
    }

    /**
     * Add a fighter after the current one
     *
     * Note: this method should not be called directly, use {@link fr.quatrevieux.araknemu.game.fight.FighterList#joinTurnList(Fighter, FightCell)} instead
     *
     * @param fighter Fighter to add
     *
     * @see TurnListChanged Event triggered after the list is updated
     */
    public void add(Fighter fighter) {
        fighters.add(index + 1, fighter);
        fight.dispatch(new TurnListChanged(this));
    }

    /**
     * Get the current turn
     *
     * @todo nullable instead of optional ?
     */
    public Optional<FightTurn> current() {
        return Optional.ofNullable(turn);
    }

    /**
     * Get the current turn fighter
     */
    public Fighter currentFighter() {
        return current;
    }

    /**
     * Start the turn system
     */
    public void start() {
        if (active.getAndSet(true)) {
            throw new IllegalStateException("TurnList already started");
        }

        index = -1;

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
        // next is called internally by turn system, and fighters if used only with active turn system
        // so fighters is always non-null
        final List<Fighter> fighters = NullnessUtil.castNonNull(this.fighters);

        turn = null;
        fight.dispatch(new NextTurnInitiated());

        while (active.get()) {
            if (++index >= fighters.size()) {
                index = 0;
            }

            if (fighters.get(index).dead()) {
                continue;
            }

            current = fighters.get(index);
            turn = new FightTurn(current, fight, fight.type().turnDuration());

            if (turn.start()) {
                break;
            }
        }
    }
}

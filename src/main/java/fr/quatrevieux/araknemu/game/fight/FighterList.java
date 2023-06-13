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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Handle the fighters list of a fight
 */
public final class FighterList implements Iterable<Fighter>, Dispatcher {
    private final Fight fight;
    private final Set<Fighter> fighters = new LinkedHashSet<>();

    public FighterList(Fight fight) {
        this.fight = fight;
    }

    @Override
    public @NonNull Iterator<Fighter> iterator() {
        return fighters.iterator();
    }

    @Override
    public void dispatch(Object event) {
        for (Fighter fighter : fighters) {
            if (fighter.isOnFight()) {
                fighter.dispatch(event);
            }
        }
    }

    /**
     * Get all fighters as collection.
     */
    public Collection<Fighter> all() {
        return Collections.unmodifiableCollection(fighters);
    }

    /**
     * Add a fighter to the fight
     *
     * @param fighter The fighter to add
     * @param cell The cell where the fighter will be placed
     *
     * @see Fighter#joinFight(Fight, FightCell) Will be called by this method
     * @see #joinTurnList(PlayableFighter, FightCell) Call this method if you want to add the fighter to the turn list
     */
    public void join(Fighter fighter, FightCell cell) {
        fighter.joinFight(fight, cell);
        fighters.add(fighter);
    }

    /**
     * Add a fighter to the fight and to the turn list
     *
     * Note: this method can only be called if the fight is active, during placement state, use {@link #join(Fighter, FightCell)} instead
     *
     * @param fighter The fighter to add
     * @param cell The cell where the fighter will be placed
     *
     * @see fr.quatrevieux.araknemu.game.fight.turn.FightTurnList#add(PlayableFighter) Will be called by this method
     */
    public void joinTurnList(PlayableFighter fighter, FightCell cell) {
        join(fighter, cell);
        fight.turnList().add(fighter);
    }

    /**
     * Remove a fighter from the fight
     * Will trigger {@link FighterRemoved} event
     *
     * Note: if the fighter is on the turn list, it will be removed too
     *
     * @param fighter The fighter to remove
     */
    public void leave(Fighter fighter) {
        fighters.remove(fighter);

        if (fight.active() && fighter instanceof PlayableFighter) {
            fight.turnList().remove((PlayableFighter) fighter);
        }

        fight.dispatch(new FighterRemoved(fighter, fight));
    }

    /**
     * Get a sequential {@code Stream} with this fighter list as its source.
     */
    public Stream<Fighter> stream() {
        return fighters.stream();
    }

    /**
     * Get all alive fighters as stream
     *
     * @see Fighter#dead()
     */
    public Stream<Fighter> alive() {
        return fighters.stream().filter(fighter -> !fighter.dead());
    }

    /**
     * Internal method for clear all fighters objects.
     */
    void clear() {
        fighters.clear();
    }
}

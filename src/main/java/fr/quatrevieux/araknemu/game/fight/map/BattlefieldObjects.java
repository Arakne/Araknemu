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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle objects added to battlefield by spells
 */
public final class BattlefieldObjects implements Iterable<BattlefieldObject> {
    private final Set<BattlefieldObject> objects = new LinkedHashSet<>();

    @Override
    @NonNull
    public Iterator<BattlefieldObject> iterator() {
        return objects.iterator();
    }

    /**
     * Get a sequential stream of objects
     */
    public Stream<BattlefieldObject> stream() {
        return objects.stream();
    }

    /**
     * Add a new object to the map
     * Note: no event will be triggered, you must handle appearance packet yourself
     *
     * @param object The object to add
     */
    public void add(BattlefieldObject object) {
        objects.add(object);
    }

    /**
     * Remove all objects matching the predicate
     *
     * @param predicate Predicate lambda. Takes as parameter the battlefield object, and return true if it must be removed
     *
     * @see BattlefieldObject#disappear() Called when object is removed
     */
    public void removeIf(Predicate<BattlefieldObject> predicate) {
        for (Iterator<BattlefieldObject> it = iterator(); it.hasNext();) {
            final BattlefieldObject object = it.next();

            if (predicate.test(object)) {
                it.remove();
                object.disappear();
            }
        }
    }

    /**
     * Remove a single object from the map
     * This method will do nothing if the object is not on the map
     *
     * @param object The object to remove
     *
     * @see BattlefieldObject#disappear() Called when object is removed
     */
    public void remove(BattlefieldObject object) {
        if (objects.remove(object)) {
            object.disappear();
        }
    }

    /**
     * Check if at least one object match the predicate
     *
     * @param predicate The predicate to test
     *
     * @return true if at least one object match the predicate
     */
    public boolean anyMatch(Predicate<BattlefieldObject> predicate) {
        for (BattlefieldObject object : objects) {
            if (predicate.test(object)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Apply start turn effects from objects :
     * - check if objects owned by fighter are still valid
     * - apply start turn effects of objects if the fighter is on area
     *
     * @param fighter The fighter who start his turn
     *
     * @see FightTurn#start() Called by this method
     * @see BattlefieldObject#onStartTurnInArea(Fighter) To apply start turn effects
     */
    public void onStartTurn(Fighter fighter) {
        for (Iterator<BattlefieldObject> it = iterator(); it.hasNext();) {
            final BattlefieldObject object = it.next();

            if (fighter.equals(object.caster())) {
                if (!object.refresh()) {
                    it.remove();
                    object.disappear();
                    continue;
                }
            }

            if (object.isOnArea(fighter)) {
                object.onStartTurnInArea(fighter);
            }
        }
    }

    /**
     * Apply end turn effects from objects if the fighter is on area
     *
     * @param fighter The fighter who end his turn
     *
     * @see BattlefieldObject#onEndTurnInArea(Fighter) To apply end turn effects
     */
    public void onEndTurn(Fighter fighter) {
        for (Iterator<BattlefieldObject> it = iterator(); it.hasNext();) {
            final BattlefieldObject object = it.next();

            if (object.isOnArea(fighter)) {
                object.onEndTurnInArea(fighter);
            }
        }
    }

    /**
     * Apply object effects when the fighter enters the object area after a move
     *
     * @param fighter The fighter who moved
     *
     * @see BattlefieldObject#onEnterInArea(Fighter) Called by this method
     * @see BattlefieldObject#isOnArea(Fighter) To check if the fighter is on area
     */
    public void onEndMove(Fighter fighter) {
        if (objects.isEmpty()) {
            return;
        }

        // Create a copy of the list to avoid ConcurrentModificationException
        final List<BattlefieldObject> toTrigger = objects.stream()
            .filter(object -> object.isOnArea(fighter))
            .collect(Collectors.toList())
        ;

        for (BattlefieldObject object : toTrigger) {
            object.onEnterInArea(fighter);
        }
    }

    /**
     * Check if the cell is blocked by an object
     *
     * @param cell The cell to check
     *
     * @return true if the movement should be stopped on the given cell
     *
     * @see BattlefieldObject#shouldStopMovement() To check if the object should stop movement
     * @see BattlefieldObject#isOnArea(FightCell) The given cell is on object's area
     */
    public boolean shouldStopMovement(FightCell cell) {
        for (BattlefieldObject object : objects) {
            if (object.shouldStopMovement() && object.isOnArea(cell)) {
                return true;
            }
        }

        return false;
    }
}

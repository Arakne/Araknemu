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
import fr.quatrevieux.araknemu.network.game.out.game.UpdateCells;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Base type for any zone object on the map
 */
public interface BattlefieldObject {
    /**
     * The cell where the object is
     *
     * Note: multiple objects can be on the same cell, so this value is not unique through the map
     */
    public FightCell cell();

    /**
     * The fighter who placed the object
     */
    public Fighter caster();

    /**
     * The size of the area
     *
     * A size of 0 means the object is on a single cell
     * The area of an object works like {@link fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea}
     */
    public @NonNegative int size();

    /**
     * The "color" argument of the object
     * This value as only display purpose. It's used by the client as layer to discriminate objects.
     */
    public int color();

    /**
     * Define custom display properties for the cell where the object is
     * This is used to define the cell object for a trap for example
     */
    public default UpdateCells.PropertyValue<?>[] cellsProperties() {
        return new UpdateCells.PropertyValue[0];
    }

    /**
     * Called on new turn of the caster
     * This method is used to decrement and check the remaining turns of the object
     *
     * Note: This method only implements decrementing the remaining turns and checking if the object is still active.
     *       It doesn't actually remove the object from the map.
     *
     * @return true if the object is still active, false otherwise
     *
     * @see BattlefieldObjects#onStartTurn(Fighter) Called by this method
     * @see BattlefieldObject#disappear() Called when the object is removed from the map
     */
    public default boolean refresh() {
        return true;
    }

    /**
     * The given fighter has started his turn in the area of the object
     *
     * @see BattlefieldObjects#onStartTurn(Fighter) Called by this method
     */
    public default void onStartTurnInArea(Fighter fighter) {}

    /**
     * The given fighter has moved into the area of the object
     *
     * A move can be:
     * - A move action
     * - A teleportation
     * - A push/pull effect
     *
     * @param fighter the fighter who moved
     */
    public default void onEnterInArea(Fighter fighter) {}

    /**
     * The object has been removed from the map
     *
     * Note: This method must not be called directly, but by the map object
     */
    public void disappear();

    /**
     * Check if the given cell is in the area of the object
     * By default, this method will check if the distance less or equal to the size of the area
     *
     * @param cell the cell to check
     *
     * @return true if the cell is in the area, false otherwise
     */
    public default boolean isOnArea(FightCell cell) {
        return cell().coordinate().distance(cell) <= size();
    }

    /**
     * Check if the given fighter is in the area of the object
     * By default, this method will check if the distance less or equal to the size of the area
     *
     * @param fighter the fighter to check
     *
     * @return true if the fighter is in the area, false otherwise
     */
    public default boolean isOnArea(Fighter fighter) {
        return isOnArea(fighter.cell());
    }

    /**
     * Make the object visible
     * This method should not be called if the object is already visible
     *
     * @param fighter Fighter who make the object visible
     */
    public default void show(Fighter fighter) {}

    /**
     * Check if the current object is visible for the given fighter
     */
    public default boolean visible(Fighter fighter) {
        return visible();
    }

    /**
     * Check if the current object is visible for all (including spectators)
     */
    public default boolean visible() {
        return true;
    }

    /**
     * Check if the object should stop the movement of the given fighter when he enters the area
     */
    public default boolean shouldStopMovement() {
        return false;
    }
}

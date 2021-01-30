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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.info;

import fr.arakne.utils.value.Interval;

/**
 * Error message
 */
final public class Error extends AbstractInformationMessage {
    public Error(Entry... entries) {
        super(Type.ERROR, entries);
    }

    public Error(int id) {
        this(new Entry(id));
    }

    public Error(int id, Object... arguments) {
        this(new Entry(id, arguments));
    }

    /**
     * Get the welcome message
     */
    static public Error welcome() {
        return new Error(89);
    }

    /**
     * Cannot do the action on this server
     */
    static public Error cantDoOnServer() {
        return new Error(226);
    }

    /**
     * Cannot do the action on the current state
     */
    static public Error cantDoOnCurrentState() {
        return new Error(116);
    }

    /**
     * Cannot learn the spell
     *
     * @param spellId The spell
     */
    static public Error cantLearnSpell(int spellId) {
        return new Error(7, spellId);
    }

    /**
     * Cannot cast the spell : not in the spell list
     */
    static public Error cantCastNotFound() {
        return new Error(169);
    }

    /**
     * Cannot cast the spell : not enough action points
     *
     * @param available The available action points
     * @param required The required action points for cast the spell
     */
    static public Error cantCastNotEnoughActionPoints(int available, int required) {
        return new Error(170, available, required);
    }

    /**
     * Cannot cast the spell : The target cell is invalid
     */
    static public Error cantCastInvalidCell() {
        return new Error(193);
    }

    /**
     * Cannot cast the spell : The target cell is not available
     */
    static public Error cantCastCellNotAvailable() {
        return new Error(172);
    }

    /**
     * Cannot cast the spell : The target cell is not in line
     */
    static public Error cantCastLineLaunch() {
        return new Error(173);
    }

    /**
     * Cannot cast the spell : The sight is blocked
     */
    static public Error cantCastSightBlocked() {
        return new Error(174);
    }

    /**
     * Cannot cast the spell : The cast is in invalid state
     */
    static public Error cantCastBadState() {
        return new Error(116);
    }

    /**
     * Cannot cast the spell : the cell is out of range
     *
     * @param range The spell range
     * @param distance The cell distance
     */
    public static Error cantCastBadRange(Interval range, int distance) {
        return new Error(171, range.min(), range.max(), distance);
    }

    /**
     * Cannot cast the spell
     */
    static public Error cantCast() {
        return new Error(175);
    }

    /**
     * Cannot perform the action during fight
     */
    static public Error cantDoDuringFight() {
        return new Error(91);
    }

    /**
     * Cannot move : the player is overweight
     */
    static public Error cantMoveOverweight() {
        return new Error(12);
    }

    /**
     * A shutdown is scheduled
     *
     * @param delay The delay string. The value is not translated.
     */
    static public Error shutdownScheduled(String delay) {
        return new Error(15, delay);
    }
}

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

import java.util.HashMap;
import java.util.Map;

/**
 * Error message
 */
public final class Error extends AbstractInformationMessage {
    private static final Map<Integer, Error> simpleErrorsByCode = new HashMap<>();

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
    public static Error welcome() {
        return forCode(89);
    }

    /**
     * Cannot do the action on this server
     */
    public static Error cantDoOnServer() {
        return forCode(226);
    }

    /**
     * Cannot do the action on the current state
     */
    public static Error cantDoOnCurrentState() {
        return forCode(116);
    }

    /**
     * Cannot learn the spell
     *
     * @param spellId The spell
     */
    public static Error cantLearnSpell(int spellId) {
        return new Error(7, spellId);
    }

    /**
     * Cannot cast the spell : not in the spell list
     */
    public static Error cantCastNotFound() {
        return forCode(169);
    }

    /**
     * Cannot cast the spell : not enough action points
     *
     * @param available The available action points
     * @param required The required action points for cast the spell
     */
    public static Error cantCastNotEnoughActionPoints(int available, int required) {
        return new Error(170, available, required);
    }

    /**
     * Cannot cast the spell : The target cell is invalid
     */
    public static Error cantCastInvalidCell() {
        return forCode(193);
    }

    /**
     * Cannot cast the spell : The target cell is not available
     */
    public static Error cantCastCellNotAvailable() {
        return forCode(172);
    }

    /**
     * Cannot cast the spell : The target cell is not in line
     */
    public static Error cantCastLineLaunch() {
        return forCode(173);
    }

    /**
     * Cannot cast the spell : The sight is blocked
     */
    public static Error cantCastSightBlocked() {
        return forCode(174);
    }

    /**
     * Cannot cast the spell : The cast is in invalid state
     */
    public static Error cantCastBadState() {
        return forCode(116);
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
    public static Error cantCast() {
        return forCode(175);
    }

    /**
     * Cannot perform the action during fight
     */
    public static Error cantDoDuringFight() {
        return forCode(91);
    }

    /**
     * Cannot move : the player is overweight
     */
    public static Error cantMoveOverweight() {
        return forCode(12);
    }

    /**
     * A shutdown is scheduled
     *
     * @param delay The delay string. The value is not translated.
     */
    public static Error shutdownScheduled(String delay) {
        return new Error(15, delay);
    }

    /**
     * A game server save is in progress
     */
    public static Error saveInProgress() {
        return forCode(164);
    }

    /**
     * The current game server save is terminated
     */
    public static Error saveTerminated() {
        return forCode(165);
    }

    /**
     * Create and cache a simple Error with a code
     *
     * @param code The error code
     *
     * @return The Error instance
     */
    private static Error forCode(int code) {
        return simpleErrorsByCode.computeIfAbsent(code, Error::new);
    }
}

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

package fr.quatrevieux.araknemu.game.fight;

/**
 * List of join fight errors
 */
public enum JoinFightError {
    CHALLENGE_FULL('c'),
    TEAM_FULL('t'),
    TEAM_DIFFERENT_ALIGNMENT('a'),
    CANT_DO_BECAUSE_GUILD('g'),
    CANT_DO_TOO_LATE('l'),
    CANT_U_ARE_MUTANT('m'),
    CANT_BECAUSE_MAP('p'),
    CANT_BECAUSE_ON_RESPAWN('r'),
    CANT_YOU_R_BUSY('o'),
    CANT_YOU_OPPONENT_BUSY('z'),
    CANT_FIGHT('h'),
    CANT_FIGHT_NO_RIGHTS('i'),
    EXPIRED_SUBSCRIPTION('s'),
    SUBSCRIPTION_OUT('n'),
    A_NOT_SUBSCRIBED('b'),
    TEAM_CLOSED('f'),
    NO_ZOMBIE_ALLOWED('d');

    private final char error;

    JoinFightError(char error) {
        this.error = error;
    }

    public char error() {
        return error;
    }
}

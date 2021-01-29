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

package fr.quatrevieux.araknemu.network.realm.out;

/**
 * An error occurs during login process
 */
final public class LoginError {
    final static public char ALREADY_LOGGED_GAME_SERVER = 'c';
    final static public char ALREADY_LOGGED = 'a';
    final static public char BANNED = 'b';
    final static public char U_DISCONNECT_ACCOUNT = 'd';
    final static public char KICKED = 'k';
    final static public char LOGIN_ERROR = 'f';
    
    final private char errorType;

    public LoginError(char errorType) {
        this.errorType = errorType;
    }

    public String toString() {
        return "AlE" + errorType;
    }
}

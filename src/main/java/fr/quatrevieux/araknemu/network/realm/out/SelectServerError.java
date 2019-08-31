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
 * Error during selecting server
 */
final public class SelectServerError {
    public enum Error {
        FULL('F'),
        CANT_SELECT('r')
        ;

        final private char c;

        Error(char c) {
            this.c = c;
        }
    }

    final private Error error;

    public SelectServerError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "AXE" + error.c;
    }
}

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

package fr.quatrevieux.araknemu.realm.authentication.password;

/**
 * Simple password algorithm using plain text (i.e. no hash)
 */
public final class PlainTextHash implements HashAlgorithm {
    class PlainTextPassword implements Password {
        private final String password;

        public PlainTextPassword(String password) {
            this.password = password;
        }

        @Override
        public boolean check(String input) {
            return password.equals(input);
        }

        @Override
        public HashAlgorithm algorithm() {
            return PlainTextHash.this;
        }

        @Override
        public boolean needRehash() {
            return false;
        }

        @Override
        public String toString() {
            return password;
        }
    }

    @Override
    public Password parse(String hashedValue) {
        return new PlainTextPassword(hashedValue);
    }

    @Override
    public boolean supports(String hashedValue) {
        return true;
    }

    @Override
    public Password hash(String inputValue) {
        return new PlainTextPassword(inputValue);
    }

    @Override
    public String name() {
        return "plain";
    }
}

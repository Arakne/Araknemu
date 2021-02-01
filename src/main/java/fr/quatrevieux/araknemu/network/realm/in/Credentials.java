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

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import org.apache.commons.lang3.StringUtils;

/**
 * Incoming login credentials
 */
public final class Credentials implements Packet {
    public enum Method {
        /** Method not used */
        NONE,
        /** Default method: vigenere cypher + pseudo base64 wrapping */
        VIGENERE_BASE_64,
        /** MD5 hash : md5( md5(user_input) + connection_key ) */
        MD5
        ;

        /**
         * Get method from method char
         *
         * @param id The race char. Should be a number
         */
        public static Method get(char id) {
            if (id > '9' || id < '0') {
                throw new NumberFormatException();
            }

            final int index = id - '0';

            if (values().length <= index) {
                throw new IndexOutOfBoundsException();
            }

            return values()[index];
        }
    }

    private final String username;
    private final String password;
    private final Method method;

    public Credentials(String username, String password, Method method) {
        this.username = username;
        this.password = password;
        this.method   = method;
    }

    public String username() {
        return username;
    }

    /**
     * Password is encrypted
     * The encryption method can be acceded using {@link Credentials#method()}
     * The encryption key can be found on {@link RealmSession#key()}
     */
    public String password() {
        return password;
    }

    /**
     * Get the cypher method
     */
    public Method method() {
        return method;
    }

    /**
     * Get the packet parser instance
     */
    public static PacketParser parser() {
        return input -> {
            final String[] parts = StringUtils.split(input, "\n", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(input, "Missing password");
            }

            final String username = parts[0].trim();
            final String hash     = parts[1].trim();

            if (hash.charAt(0) != '#') {
                throw new ParsePacketException(input, "Invalid hash format");
            }

            try {
                return new Credentials(
                    username,
                    hash.substring(2),
                    Method.get(hash.charAt(1))
                );
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                throw new ParsePacketException(input, "Invalid cypher method");
            }
        };
    }
}

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

package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Packet for log into Game server
 */
public final class LoginToken implements Packet {
    private final String token;

    public LoginToken(String token) {
        this.token = token;
    }

    /**
     * Get the requested token value
     */
    public String token() {
        return token;
    }

    public static final class Parser implements SinglePacketParser<LoginToken> {
        @Override
        public LoginToken parse(String input) throws ParsePacketException {
            return new LoginToken(input);
        }

        @Override
        public String code() {
            return "AT";
        }
    }
}

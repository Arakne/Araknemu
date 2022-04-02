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

package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Response for RPing request
 */
public final class PingResponse implements Packet {
    private final String payload;

    public PingResponse(String payload) {
        this.payload = payload;
    }

    /**
     * Get the response payload
     */
    public String payload() {
        return payload;
    }

    public static class Parser implements SinglePacketParser<PingResponse> {
        @Override
        public PingResponse parse(String input) throws ParsePacketException {
            return new PingResponse(input);
        }

        @Override
        public @MinLen(2) String code() {
            return "rpong";
        }
    }
}

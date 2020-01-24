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

package fr.quatrevieux.araknemu.network.game.in.basic.admin;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import org.apache.commons.lang3.StringUtils;

/**
 * Teleport to the requested map
 * The move use simple x,y coordinates
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L23
 */
final public class AdminMove implements Packet {
    final static public class Parser implements SinglePacketParser<AdminMove> {
        @Override
        public AdminMove parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, ",", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Missing coordinates");
            }

            return new AdminMove(
                new Geolocation(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
                )
            );
        }

        @Override
        public String code() {
            return "BaM";
        }
    }

    final private Geolocation geolocation;

    public AdminMove(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    /**
     * Get the target geolocation
     */
    public Geolocation geolocation() {
        return geolocation;
    }
}

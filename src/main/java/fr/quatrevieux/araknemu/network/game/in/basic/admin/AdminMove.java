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
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Teleport to the requested map
 * The move use simple x,y coordinates
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L23
 */
public final class AdminMove implements Packet {
    private final Geolocation geolocation;

    public AdminMove(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    /**
     * Get the target geolocation
     */
    public Geolocation geolocation() {
        return geolocation;
    }

    public static final class Parser implements SinglePacketParser<AdminMove> {
        @Override
        public AdminMove parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, ',');

            return new AdminMove(
                new Geolocation(
                    tokenizer.nextInt(),
                    tokenizer.nextInt()
                )
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "BaM";
        }
    }
}

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
 * Copyright (c) 2017-2026 Leor Finacre
 */
package fr.quatrevieux.araknemu.network.game.in.social.enemy;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.common.value.qual.MinLen;

public class EnemyAddRequest implements Packet {
    private final String pseudo;

    public EnemyAddRequest(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Get the search pseudo
     */
    public String pseudo() {
        return pseudo;
    }

    public static final class Parser implements SinglePacketParser<EnemyAddRequest> {

        @Override
        public EnemyAddRequest parse(String input) throws ParsePacketException {
            return new EnemyAddRequest(input.replace("%", ""));
        }

        @Override
        public @MinLen(2) String code() {
            return "iA";
        }
    }
}
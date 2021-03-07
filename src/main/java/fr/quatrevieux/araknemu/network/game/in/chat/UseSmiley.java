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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.in.chat;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Send a smiley to the map / fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L140
 */
public final class UseSmiley implements Packet {
    private final int smiley;

    public UseSmiley(int smiley) {
        this.smiley = smiley;
    }

    /**
     * Get the smiley id
     * Smileys are located on "clips/smileys/[id].swf" on the Dofus client
     */
    public int smiley() {
        return smiley;
    }

    public static final class Parser implements SinglePacketParser<UseSmiley> {
        @Override
        public UseSmiley parse(String input) throws ParsePacketException {
            return new UseSmiley(Integer.parseInt(input));
        }

        @Override
        public String code() {
            return "BS";
        }
    }
}

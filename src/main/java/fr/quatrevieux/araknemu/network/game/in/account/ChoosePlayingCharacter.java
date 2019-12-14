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
 * Select the character for playing
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L84
 */
final public class ChoosePlayingCharacter implements Packet {
    final static public class Parser implements SinglePacketParser<ChoosePlayingCharacter> {
        @Override
        public ChoosePlayingCharacter parse(String input) throws ParsePacketException {
            return new ChoosePlayingCharacter(
                Integer.parseInt(input)
            );
        }

        @Override
        public String code() {
            return "AS";
        }
    }

    final private int id;

    public ChoosePlayingCharacter(int id) {
        this.id = id;
    }

    /**
     * Get the character ID
     */
    public int id() {
        return id;
    }
}

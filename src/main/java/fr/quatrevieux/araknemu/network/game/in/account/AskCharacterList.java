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
 * Ask for the character list
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L63
 */
final public class AskCharacterList implements Packet {
    final static public class Parser implements SinglePacketParser<AskCharacterList> {
        @Override
        public AskCharacterList parse(String input) throws ParsePacketException {
            return new AskCharacterList(
                input.equals("f")
            );
        }

        @Override
        public String code() {
            return "AL";
        }
    }

    final private boolean forced;

    public AskCharacterList(boolean forced) {
        this.forced = forced;
    }

    /**
     * Force ask for character list
     */
    public boolean forced() {
        return forced;
    }
}

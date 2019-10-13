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

package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Change fight start place
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L31
 */
final public class FighterChangePlace implements Packet {
    final static public class Parser implements SinglePacketParser<FighterChangePlace> {
        @Override
        public FighterChangePlace parse(String input) throws ParsePacketException {
            return new FighterChangePlace(Integer.parseUnsignedInt(input));
        }

        @Override
        public String code() {
            return "Gp";
        }
    }

    final private int cellId;

    public FighterChangePlace(int cellId) {
        this.cellId = cellId;
    }

    public int cellId() {
        return cellId;
    }
}

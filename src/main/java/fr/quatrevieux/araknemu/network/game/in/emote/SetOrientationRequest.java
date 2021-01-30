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

package fr.quatrevieux.araknemu.network.game.in.emote;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Change the player sprite orientation on exploration
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Emotes.as#L32
 */
final public class SetOrientationRequest implements Packet {
    final static public class Parser implements SinglePacketParser<SetOrientationRequest> {
        @Override
        public SetOrientationRequest parse(String input) throws ParsePacketException {
            final int number = input.charAt(0) - '0';

            if (number >= Direction.values().length) {
                throw new ParsePacketException(code() + input, "Invalid direction");
            }

            return new SetOrientationRequest(Direction.values()[number]);
        }

        @Override
        public String code() {
            return "eD";
        }
    }

    final private Direction orientation;

    public SetOrientationRequest(Direction orientation) {
        this.orientation = orientation;
    }

    public Direction orientation() {
        return orientation;
    }
}

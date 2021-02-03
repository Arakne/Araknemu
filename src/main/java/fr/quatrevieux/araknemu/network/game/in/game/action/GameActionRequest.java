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

package fr.quatrevieux.araknemu.network.game.in.game.action;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for start a game action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L19
 */
public final class GameActionRequest implements Packet {
    private final int type;
    private final String[] arguments;

    public GameActionRequest(int type, String[] arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public int type() {
        return type;
    }

    public String[] arguments() {
        return arguments;
    }

    public static final class Parser implements SinglePacketParser<GameActionRequest> {
        @Override
        public GameActionRequest parse(String input) throws ParsePacketException {
            return new GameActionRequest(
                Integer.parseInt(input.substring(0, 3)),
                StringUtils.split(input.substring(3), ";")
            );
        }

        @Override
        public String code() {
            return "GA";
        }
    }
}

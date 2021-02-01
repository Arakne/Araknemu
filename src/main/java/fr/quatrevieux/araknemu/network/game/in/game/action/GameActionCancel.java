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
 * Cancel the current game action, and all its followers
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L28
 */
public final class GameActionCancel implements Packet {
    public static final class Parser implements SinglePacketParser<GameActionCancel> {
        @Override
        public GameActionCancel parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException("GKE" + input, "The packet should have 2 parts separated by a pipe");
            }

            return new GameActionCancel(
                Integer.parseInt(parts[0]),
                parts[1]
            );
        }

        @Override
        public String code() {
            return "GKE";
        }
    }

    private final int actionId;
    private final String argument;

    public GameActionCancel(int actionId, String argument) {
        this.actionId = actionId;
        this.argument = argument;
    }

    public int actionId() {
        return actionId;
    }

    public String argument() {
        return argument;
    }
}

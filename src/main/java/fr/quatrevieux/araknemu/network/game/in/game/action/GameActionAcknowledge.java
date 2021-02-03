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

/**
 * The game action is successfully executed by the client
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L24
 */
public final class GameActionAcknowledge implements Packet {
    private final int actionId;

    public GameActionAcknowledge(int actionId) {
        this.actionId = actionId;
    }

    public int actionId() {
        return actionId;
    }

    public static final class Parser implements SinglePacketParser<GameActionAcknowledge> {
        @Override
        public GameActionAcknowledge parse(String input) throws ParsePacketException {
            return new GameActionAcknowledge(
                Integer.parseInt(input)
            );
        }

        @Override
        public String code() {
            return "GKK";
        }
    }
}

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

package fr.quatrevieux.araknemu.network.game.in.game;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Ask for create new game session
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L23
 */
public final class CreateGameRequest implements Packet {
    public enum Type {
        NONE,
        EXPLORATION,
        FIGHT
    }

    public static final class Parser implements SinglePacketParser<CreateGameRequest> {
        @Override
        public CreateGameRequest parse(String input) throws ParsePacketException {
            return new CreateGameRequest(
                Type.values()[input.charAt(0) - '0']
            );
        }

        @Override
        public String code() {
            return "GC";
        }
    }

    private final Type type;

    public CreateGameRequest(Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }
}

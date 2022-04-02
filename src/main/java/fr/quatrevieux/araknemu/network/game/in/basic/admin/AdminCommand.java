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

package fr.quatrevieux.araknemu.network.game.in.basic.admin;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Admin console command
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L21
 */
public final class AdminCommand implements Packet {
    private final String command;

    public AdminCommand(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }

    public static final class Parser implements SinglePacketParser<AdminCommand> {
        @Override
        public AdminCommand parse(String input) throws ParsePacketException {
            return new AdminCommand(input);
        }

        @Override
        public @MinLen(2) String code() {
            return "BA";
        }
    }
}

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
import org.apache.commons.lang3.StringUtils;

/**
 * Ask for character deletion
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L94
 */
final public class DeleteCharacterRequest implements Packet {
    final static public class Parser implements SinglePacketParser<DeleteCharacterRequest> {
        @Override
        public DeleteCharacterRequest parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            return new DeleteCharacterRequest(
                Integer.parseInt(parts[0]),
                parts.length == 2 ? parts[1] : ""
            );
        }

        @Override
        public String code() {
            return "AD";
        }
    }

    final private int id;
    final private String answer;

    public DeleteCharacterRequest(int id, String answer) {
        this.id = id;
        this.answer = answer;
    }

    public int id() {
        return id;
    }

    public String answer() {
        return answer;
    }
}

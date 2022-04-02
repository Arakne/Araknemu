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
import org.checkerframework.common.value.qual.MinLen;

/**
 * Ask for account gifts
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L128
 */
public final class AskGift implements Packet {
    private final String language;

    public AskGift(String language) {
        this.language = language;
    }

    public String language() {
        return language;
    }

    public static final class Parser implements SinglePacketParser<AskGift> {
        @Override
        public AskGift parse(String input) throws ParsePacketException {
            return new AskGift(input);
        }

        @Override
        public @MinLen(2) String code() {
            return "Ag";
        }
    }
}

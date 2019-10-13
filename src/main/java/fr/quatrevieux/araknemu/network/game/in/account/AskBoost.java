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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Boost one characteristic
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L111
 */
final public class AskBoost implements Packet {
    final static public class Parser implements SinglePacketParser<AskBoost> {
        @Override
        public AskBoost parse(String input) throws ParsePacketException {
            return new AskBoost(
                Characteristic.fromId(
                    Integer.parseInt(input)
                )
            );
        }

        @Override
        public String code() {
            return "AB";
        }
    }

    final private Characteristic characteristic;

    public AskBoost(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    public Characteristic characteristic() {
        return characteristic;
    }
}

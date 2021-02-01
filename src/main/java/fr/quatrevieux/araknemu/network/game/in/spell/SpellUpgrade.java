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

package fr.quatrevieux.araknemu.network.game.in.spell;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Ask for upgrade a spell
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L23
 */
public final class SpellUpgrade implements Packet {
    public static final class Parser implements SinglePacketParser<SpellUpgrade> {
        @Override
        public SpellUpgrade parse(String input) throws ParsePacketException {
            return new SpellUpgrade(Integer.parseUnsignedInt(input));
        }

        @Override
        public String code() {
            return "SB";
        }
    }

    private final int spellId;

    public SpellUpgrade(int spellId) {
        this.spellId = spellId;
    }

    public int spellId() {
        return spellId;
    }
}

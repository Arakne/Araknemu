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
import org.apache.commons.lang3.StringUtils;

/**
 * Spell move to spell bar
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L19
 */
public final class SpellMove implements Packet {
    private final int spellId;
    private final int position;

    public SpellMove(int spellId, int position) {
        this.spellId = spellId;
        this.position = position;
    }

    public int spellId() {
        return spellId;
    }

    public int position() {
        return position;
    }

    public static final class Parser implements SinglePacketParser<SpellMove> {
        @Override
        public SpellMove parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            return new SpellMove(
                Integer.parseUnsignedInt(parts[0]),
                Integer.parseUnsignedInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "SM";
        }
    }
}

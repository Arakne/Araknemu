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
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Spell move to spell bar
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L19
 */
public final class SpellMove implements Packet {
    private final @NonNegative int spellId;
    private final @IntRange(from = 1, to = 63) int position;

    public SpellMove(@NonNegative int spellId, @IntRange(from = 1, to = 63) int position) {
        this.spellId = spellId;
        this.position = position;
    }

    /**
     * Spell to move
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.SpellTemplate#id()
     */
    public @NonNegative int spellId() {
        return spellId;
    }

    /**
     * The requested position
     * This value is always positive, because position "0" is reserved for close combat
     *
     * @see fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell#position()
     */
    public @IntRange(from = 1, to = 63) int position() {
        return position;
    }

    public static final class Parser implements SinglePacketParser<SpellMove> {
        @Override
        public SpellMove parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, '|');

            final int spellId = tokenizer.nextNonNegativeInt();
            final int position = tokenizer.nextPositiveInt();

            if (position > 63) {
                throw new ParsePacketException(code() + input, "Invalid spell position " + position);
            }

            return new SpellMove(spellId, position);
        }

        @Override
        public @MinLen(2) String code() {
            return "SM";
        }
    }
}

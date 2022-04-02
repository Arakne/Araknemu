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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.in.account;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Request for create a new character
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L90
 */
public final class AddCharacterRequest implements Packet {
    private final String name;
    private final Race race;
    private final Gender gender;
    private final Colors colors;

    public AddCharacterRequest(String name, Race race, Gender gender, Colors colors) {
        this.name = name;
        this.race = race;
        this.gender = gender;
        this.colors = colors;
    }

    public String name() {
        return name;
    }

    public Race race() {
        return race;
    }

    public Gender gender() {
        return gender;
    }

    public Colors colors() {
        return colors;
    }

    public static final class Parser implements SinglePacketParser<AddCharacterRequest> {
        @Override
        public AddCharacterRequest parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, '|');

            return new AddCharacterRequest(
                tokenizer.nextPart(),
                Race.byId(tokenizer.nextPositiveInt()),
                Gender.parse(tokenizer.nextPart()),
                new Colors(
                    checkColor(tokenizer.nextInt()),
                    checkColor(tokenizer.nextInt()),
                    checkColor(tokenizer.nextInt())
                )
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "AA";
        }

        private static @IntRange(from = -1, to = Colors.MAX_COLOR_VALUE) int checkColor(int value) {
            if (value < -1 || value > Colors.MAX_COLOR_VALUE) {
                throw new ParsePacketException("AA", "Invalid color value");
            }

            return value;
        }
    }
}

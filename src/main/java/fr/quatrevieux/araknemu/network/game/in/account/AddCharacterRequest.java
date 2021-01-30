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
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for create a new character
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L90
 */
final public class AddCharacterRequest implements Packet {
    final static public class Parser implements SinglePacketParser<AddCharacterRequest> {
        @Override
        public AddCharacterRequest parse(String input) throws ParsePacketException {
            final String[] data = StringUtils.split(input, "|", 6);

            if (data.length != 6) {
                throw new ParsePacketException(code() + input, "Invalid data : required 6 parts");
            }

            return new AddCharacterRequest(
                data[0],
                Race.byId(Integer.parseInt(data[1])),
                Gender.parse(data[2]),
                new Colors(
                    Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]),
                    Integer.parseInt(data[5])
                )
            );
        }

        @Override
        public String code() {
            return "AA";
        }
    }

    final private String name;
    final private Race race;
    final private Gender gender;
    final private Colors colors;

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
}

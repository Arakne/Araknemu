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

package fr.quatrevieux.araknemu.network.game.in.dialog;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.dataflow.qual.Pure;

/**
 * A dialog response is chosen by the player
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Dialog.as#L31
 */
public final class ChosenResponse implements Packet {
    private final int question;
    private final int response;

    public ChosenResponse(int question, int response) {
        this.question = question;
        this.response = response;
    }

    @Pure
    public int question() {
        return question;
    }

    @Pure
    public int response() {
        return response;
    }

    public static final class Parser implements SinglePacketParser<ChosenResponse> {
        @Override
        public ChosenResponse parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Expects two parts");
            }

            return new ChosenResponse(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "DR";
        }
    }
}

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

package fr.quatrevieux.araknemu.network.game.in.info;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Send the screen information (size, and state)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Infos.as#L23
 */
public final class ScreenInfo implements Packet {
    public enum State {
        NORMAL,
        FULLSCREEN,
        OTHER
    }

    private final int width;
    private final int height;
    private final State state;

    public ScreenInfo(int width, int height, State state) {
        this.width = width;
        this.height = height;
        this.state = state;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public State state() {
        return state;
    }

    public static final class Parser implements SinglePacketParser<ScreenInfo> {
        @Override
        public ScreenInfo parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, ";", 3);

            if (parts.length != 3) {
                throw new ParsePacketException("Ir" + input, "Screen info must be composed of 3 parts");
            }

            return new ScreenInfo(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                State.values()[parts[2].charAt(0) - '0']
            );
        }

        @Override
        public String code() {
            return "Ir";
        }
    }
}

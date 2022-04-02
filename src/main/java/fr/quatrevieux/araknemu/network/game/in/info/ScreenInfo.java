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
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.util.ParseUtils;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.MinLen;

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

    private final @Positive int width;
    private final @Positive int height;
    private final State state;

    public ScreenInfo(@Positive int width, @Positive int height, State state) {
        this.width = width;
        this.height = height;
        this.state = state;
    }

    public @Positive int width() {
        return width;
    }

    public @Positive int height() {
        return height;
    }

    public State state() {
        return state;
    }

    public static final class Parser implements SinglePacketParser<ScreenInfo> {
        private final State[] states = State.values();

        @Override
        public ScreenInfo parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, ';');

            final int width = tokenizer.nextPositiveInt();
            final int height = tokenizer.nextPositiveInt();
            final int state = Math.min(ParseUtils.parseDecimalChar(tokenizer.nextPart()), states.length - 1);

            return new ScreenInfo(width, height, states[state]);
        }

        @Override
        public @MinLen(2) String code() {
            return "Ir";
        }
    }
}

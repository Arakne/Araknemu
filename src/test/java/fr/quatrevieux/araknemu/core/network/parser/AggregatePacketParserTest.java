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

package fr.quatrevieux.araknemu.core.network.parser;

import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggregatePacketParserTest {
    static public class ParserStub implements SinglePacketParser<Packet> {
        public String input;

        @Override
        public Packet parse(String input) throws ParsePacketException {
            this.input = input;

            return null;
        }

        @Override
        public String code() {
            return "TEST";
        }
    }

    @Test
    public void packetNotFound() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{});

        assertThrows(UndefinedPacketException.class, () -> parser.parse("not found"));
    }

    @Test
    public void parseSuccess() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{
            new AskQueuePosition.Parser()
        });

        assertTrue(parser.parse("Af") instanceof AskQueuePosition);
    }

    @Test
    public void parseSuccessWithArgument() {
        ParserStub stub = new ParserStub();

        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{
            stub
        });

        parser.parse("TEST123");

        assertEquals("123", stub.input);
    }

    @Test
    public void register() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{});
        assertThrows(UndefinedPacketException.class, () -> parser.parse("Af"));

        parser.register(new AskQueuePosition.Parser());
        assertTrue(parser.parse("Af") instanceof AskQueuePosition);
    }
}

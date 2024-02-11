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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    static public class ParserCodeStub implements SinglePacketParser<Packet> {
        private final String code;

        public ParserCodeStub(String code) {
            this.code = code;
        }

        @Override
        public Packet parse(String input) throws ParsePacketException {
            return new Packet() {
                @Override
                public String toString() {
                    return code + "+" + input;
                }
            };
        }

        @Override
        public String code() {
            return code;
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

    @Test
    void withSamePrefix() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{
            new ParserCodeStub("A"),
            new ParserCodeStub("AA"),
            new ParserCodeStub("AB"),
        });

        assertEquals("A+", parser.parse("A").toString());
        assertEquals("AA+", parser.parse("AA").toString());
        assertEquals("AA+AA", parser.parse("AAAA").toString());
        assertEquals("AB+AA", parser.parse("ABAA").toString());
        assertEquals("A+CAA", parser.parse("ACAA").toString());
    }

    @Test
    void registerOutOfCharset() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{});

        assertThrows(IllegalArgumentException.class, () -> parser.register(new ParserCodeStub("00000")));
        assertThrows(IllegalArgumentException.class, () -> parser.register(new ParserCodeStub("||")));
    }

    @Test
    void shouldStopResolveOnOutOfCharset() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{
            new ParserCodeStub("GA"),
            new ParserCodeStub("GKK"),
            new ParserCodeStub("GKE"),
        });

        assertEquals("GA+500", parser.parse("GA500").toString());
        assertEquals("GKE+|12", parser.parse("GKE|12").toString());
    }
}

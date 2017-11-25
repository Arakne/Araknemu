package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.network.realm.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.ChooseServer;
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

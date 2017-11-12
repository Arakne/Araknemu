package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.network.realm.in.AskQueuePosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggregatePacketParserTest {
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
    public void register() {
        AggregatePacketParser parser = new AggregatePacketParser(new SinglePacketParser[]{});
        assertThrows(UndefinedPacketException.class, () -> parser.parse("Af"));

        parser.register(new AskQueuePosition.Parser());
        assertTrue(parser.parse("Af") instanceof AskQueuePosition);
    }
}

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealmPacketParserTest {
    private RealmPacketParser parser;

    @BeforeEach
    void setUp() {
        parser = new RealmPacketParser(
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            )
        );
    }

    @Test
    void loginProcedure() {
        Packet packet = parser.parse("1.29.1");

        assertTrue(packet instanceof DofusVersion);
        assertEquals("1.29.1", DofusVersion.class.cast(packet).version());

        packet = parser.parse("authenticate\n#1password");

        assertTrue(packet instanceof Credentials);
        assertEquals("authenticate", Credentials.class.cast(packet).username());

        assertTrue(parser.parse("Af") instanceof AskQueuePosition);
    }

    @Test
    void invalidPacketDuringLogin() {
        parser.parse("1.29.1");

        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }
}

package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.network.in._testpackets.OtherPacket;
import fr.quatrevieux.araknemu.network.in._testpackets.PacketWithParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PackageParserLoaderTest {
    @Test
    void loadWithFoundPackage() {
        PackageParserLoader loader = new PackageParserLoader("fr.quatrevieux.araknemu.network.in._testpackets");

        Collection<SinglePacketParser> parsers = loader.load();

        assertEquals(2, parsers.size());
        assertTrue(parsers.toArray()[0] instanceof OtherPacket.Parser);
        assertTrue(parsers.toArray()[1] instanceof PacketWithParser.Parser);
    }

    @Test
    void loadWithNotFoundPackage() {
        PackageParserLoader loader = new PackageParserLoader("not_found");

        assertTrue(loader.load().isEmpty());
    }
}

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DofusVersionTest {
    @Test
    void parser() {
        Packet version = DofusVersion.parser().parse("1.29.1");

        assertTrue(version instanceof DofusVersion);
        assertEquals("1.29.1", DofusVersion.class.cast(version).version());
    }
}

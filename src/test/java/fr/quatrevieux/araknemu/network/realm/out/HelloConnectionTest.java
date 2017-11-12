package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.realm.ConnectionKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloConnectionTest {
    @Test
    void test_toString() {
        assertEquals("HCmykey", new HelloConnection(new ConnectionKey("mykey")).toString());
    }
}
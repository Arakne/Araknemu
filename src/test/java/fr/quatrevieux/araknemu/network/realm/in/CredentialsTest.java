package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {
    @Test
    public void success() {
        Packet packet = Credentials.parser().parse("authenticate\n#1my_hash");

        assertTrue(packet instanceof Credentials);

        Credentials credentials = (Credentials) packet;
        assertEquals("authenticate", credentials.username());
        assertEquals(Credentials.Method.VIGENERE_BASE_64, credentials.method());
        assertEquals("my_hash", credentials.password());
    }

    @Test
    public void noPassword() {
        assertThrows(ParsePacketException.class, () -> Credentials.parser().parse("invalid"), "Missing password");
    }

    @Test
    public void invalidHashFormat() {
        assertThrows(ParsePacketException.class, () -> Credentials.parser().parse("authenticate\ninvalid"), "Invalid hash format");
    }

    @Test
    public void invalidMethod() {
        assertThrows(ParsePacketException.class, () -> Credentials.parser().parse("authenticate\n#8hash"), "Invalid cypher method");
    }

    @Test
    public void methodGetInvalidChar() {
        assertThrows(NumberFormatException.class, () -> Credentials.Method.get('a'));
    }

    @Test
    public void methodGetInvalidMethod() {
        assertThrows(IndexOutOfBoundsException.class, () -> Credentials.Method.get('5'));
    }

    @Test
    public void methodGetValid() {
        assertEquals(Credentials.Method.NONE, Credentials.Method.get('0'));
        assertEquals(Credentials.Method.VIGENERE_BASE_64, Credentials.Method.get('1'));
        assertEquals(Credentials.Method.MD5, Credentials.Method.get('2'));
    }
}

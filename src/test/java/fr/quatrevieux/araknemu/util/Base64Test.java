package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

class Base64Test {
    @Test
    void ordSuccess() {
        String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        for (int i = 0; i < charset.length(); ++i) {
            assertEquals(i, Base64.ord(charset.charAt(i)));
        }
    }

    @Test
    void ordInvalidChar() {
        assertThrows(InvalidParameterException.class, () -> Base64.ord('#'));
    }

    @Test
    void encodeSingleChar() {
        String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        for (int i = 0; i < charset.length(); ++i) {
            assertEquals(Character.toString(charset.charAt(i)), Base64.encode(i, 1));
        }
    }

    @Test
    void encodeWithTwoChars() {
        assertEquals("cr", Base64.encode(145, 2));
    }

    @Test
    void encodeWithTooSmallNumberWillKeepLength() {
        assertEquals("aac", Base64.encode(2, 3));
    }

    @Test
    void chr() {
        assertEquals('a', Base64.chr(0));
        assertEquals('_', Base64.chr(63));
        assertEquals('c', Base64.chr(2));
    }

    @Test
    void decodeWithOneChar() {
        assertEquals(0, Base64.decode("a"));
        assertEquals(2, Base64.decode("c"));
        assertEquals(63, Base64.decode("_"));
    }

    @Test
    void decode() {
        assertEquals(458, Base64.decode("hk"));
    }

    @Test
    void decodeEncodeTwoChars() {
        assertEquals(741, Base64.decode(Base64.encode(741, 2)));
        assertEquals(951, Base64.decode(Base64.encode(951, 2)));
        assertEquals(325, Base64.decode(Base64.encode(325, 2)));
        assertEquals(769, Base64.decode(Base64.encode(769, 2)));
    }
}

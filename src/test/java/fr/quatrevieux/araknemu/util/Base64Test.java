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
}

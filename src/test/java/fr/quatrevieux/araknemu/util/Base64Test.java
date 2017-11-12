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
}
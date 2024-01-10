/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class WeaponsAbilitiesTransformerTest {
    private WeaponsAbilitiesTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new WeaponsAbilitiesTransformer();
    }

    @Test
    void unserialize() {
        assertEquals(new HashMap<>(), transformer.unserialize("{}"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 456); }}, transformer.unserialize("{\"123\":456}"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 456); put(741, 963); }}, transformer.unserialize("{\"123\":456,\"741\":963}"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 456); }}, transformer.unserialize("{\"123\":\"456\"}"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 456); }}, transformer.unserialize("{123:\"456\"}"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 321); }}, transformer.unserialize("{123:321}"));

        try {
            transformer.unserialize("");
            fail("Expected TransformerException");
        } catch (TransformerException e) {
            assertEquals("Null or empty value is not allowed", e.getMessage());
        }

        try {
            transformer.unserialize("{ffdsd");
            fail("Expected TransformerException");
        } catch (TransformerException e) {
            assertEquals("Invalid JSON : {ffdsd", e.getMessage());
        }

        try {
            transformer.unserialize("{\"foo\":123}");
            fail("Expected TransformerException");
        } catch (TransformerException e) {
            assertEquals("Invalid JSON : {\"foo\":123}", e.getMessage());
        }

        try {
            transformer.unserialize("{123:\"foo\"}");
            fail("Expected TransformerException");
        } catch (TransformerException e) {
            assertEquals("Invalid JSON : {123:\"foo\"}", e.getMessage());
        }

        try {
            transformer.unserialize("{123:-456}");
            fail("Expected TransformerException");
        } catch (TransformerException e) {
            assertEquals("Invalid JSON value : {123:-456}", e.getMessage());
        }
    }

    @Test
    void serialize() {
        assertEquals("{}", transformer.serialize(new HashMap<>()));
        assertEquals("{\"123\":456}", transformer.serialize(new HashMap<Integer, Integer>() {{ put(123, 456); }}));
        assertEquals("{\"741\":963,\"123\":456}", transformer.serialize(new HashMap<Integer, Integer>() {{ put(123, 456); put(741, 963); }}));
    }

    @Test
    void withNull() {
        assertNull(transformer.serialize(null));
        assertNull(transformer.unserialize(null));
    }
}

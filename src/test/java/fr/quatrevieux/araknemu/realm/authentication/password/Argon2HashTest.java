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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm.authentication.password;

import de.mkammerer.argon2.Argon2Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Argon2HashTest {
    private Argon2Hash hash;

    @BeforeEach
    void setUp() {
        hash = new Argon2Hash();
    }

    @Test
    void hash() {
        Password password = hash.hash("test");

        assertSame(hash, password.algorithm());
        assertTrue(password.check("test"));
        assertFalse(password.check("invalid"));
        assertFalse(password.needRehash());
        assertTrue(password.toString().startsWith("$argon2id$v=19$m=65536,t=4,p=8$"));
    }

    @Test
    void parse() {
        Password password = hash.parse("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU");

        assertSame(hash, password.algorithm());
        assertTrue(password.check("test"));
        assertFalse(password.check("invalid"));
        assertFalse(password.needRehash());
        assertEquals("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU", password.toString());
    }

    @Test
    void supports() {
        assertTrue(hash.supports("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU"));
        assertTrue(hash.supports("$argon2i$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU"));
        assertFalse(hash.supports("test"));
    }

    @Test
    void name() {
        assertEquals("argon2", hash.name());
    }

    @Test
    void needRehash() {
        Password password = hash.parse("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU");

        assertFalse(password.needRehash());

        hash.setMemory(32000);
        assertFalse(password.needRehash());

        hash.setMemory(128000);
        assertTrue(password.needRehash());

        hash
            .setMemory(65536)
            .setType(Argon2Factory.Argon2Types.ARGON2i)
        ;

        assertTrue(password.needRehash());
    }

    @Test
    void typesInteroperability() {
        Password password = hash.hash("test");

        Argon2Hash otherHash = new Argon2Hash().setType(Argon2Factory.Argon2Types.ARGON2i);
        Password passwordOnOtherHash = otherHash.parse(password.toString());

        assertTrue(passwordOnOtherHash.check("test"));
        assertTrue(passwordOnOtherHash.needRehash());
    }

    @Test
    void parameters() {
        assertTrue(hash.hash("test").toString().startsWith("$argon2id$v=19$m=65536,t=4,p=8$"));
        assertTrue(hash.setType(Argon2Factory.Argon2Types.ARGON2i).hash("test").toString().startsWith("$argon2i$v=19$m=65536,t=4,p=8$"));
        assertTrue(hash.setMemory(54000).hash("test").toString().startsWith("$argon2i$v=19$m=54000,t=4,p=8$"));
        assertTrue(hash.setIterations(5).hash("test").toString().startsWith("$argon2i$v=19$m=54000,t=5,p=8$"));
        assertTrue(hash.setParallelism(3).hash("test").toString().startsWith("$argon2i$v=19$m=54000,t=5,p=3$"));
    }

    @Test
    void typeByName() {
        assertSame(Argon2Factory.Argon2Types.ARGON2d, Argon2Hash.typeByName("argon2d"));
        assertSame(Argon2Factory.Argon2Types.ARGON2i, Argon2Hash.typeByName("argon2i"));
        assertSame(Argon2Factory.Argon2Types.ARGON2id, Argon2Hash.typeByName("argon2id"));
        assertThrows(NoSuchElementException.class, () -> Argon2Hash.typeByName("not found"));
    }
}

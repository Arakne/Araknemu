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

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class PasswordManagerTest extends TestCase {
    private PasswordManager manager;

    @BeforeEach
    void setUp() {
        manager = new PasswordManager(
            Arrays.asList("argon2", "plain"),
            new Argon2Hash(),
            new PlainTextHash()
        );
    }

    @Test
    void constructorWithInvalidAlgo() {
        assertThrowsWithMessage(IllegalArgumentException.class, "Hash algorithms [invalid, plain] are not registered", () -> new PasswordManager(
            Arrays.asList("argon2", "invalid", "plain"),
            new Argon2Hash()
        ));
    }

    @Test
    void get() {
        assertInstanceOf(PlainTextHash.PlainTextPassword.class, manager.get("plain"));
        assertEquals("plain", manager.get("plain").toString());
        assertInstanceOf(Argon2Hash.Argon2Password.class, manager.get("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU"));
        assertEquals("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU", manager.get("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU").toString());
        assertTrue(manager.get("$argon2id$v=19$m=65536,t=4,p=8$VD0Sb6gphmn8Nzqrq+SBzA$5QJbvXjQSFzeM9oFhqYLS73uQS/2K0q4WUX1Fgvr+uU").check("test"));
    }

    @Test
    void getUnsupported() {
        manager = new PasswordManager(
            Arrays.asList("argon2"),
            new Argon2Hash()
        );

        assertThrows(IllegalArgumentException.class, () -> manager.get("invalid"));
    }

    @Test
    void hash() {
        Password password = manager.hash("test");

        assertInstanceOf(Argon2Hash.Argon2Password.class, password);
        assertTrue(password.toString().startsWith("$argon2id$v=19$m=65536,t=4,p=8$"));
        assertTrue(password.check("test"));
    }

    @Test
    void hashWithPlainTextAsDefault() {
        manager = new PasswordManager(
            Arrays.asList("plain"),
            new PlainTextHash()
        );

        Password password = manager.hash("test");
        assertInstanceOf(PlainTextHash.PlainTextPassword.class, password);
        assertEquals("test", password.toString());
    }

    @Test
    void rehash() {
        Password password = manager.hash("test");

        Consumer<Password> action = Mockito.mock(Consumer.class);
        manager.rehash(password, "test", action);

        Mockito.verify(action, Mockito.never()).accept(Mockito.any());

        manager = new PasswordManager(
            Arrays.asList("argon2"),
            new Argon2Hash().setIterations(5)
        );
        password = manager.get(password.toString());

        AtomicReference<Password> newPass = new AtomicReference<>();

        manager.rehash(password, "test", newPass::set);
        assertTrue(newPass.get().toString().startsWith("$argon2id$v=19$m=65536,t=5,p=8$"));

        manager = new PasswordManager(
            Arrays.asList("plain"),
            new PlainTextHash()
        );

        manager.rehash(password, "test", newPass::set);
        assertEquals("test", newPass.get().toString());
    }
}

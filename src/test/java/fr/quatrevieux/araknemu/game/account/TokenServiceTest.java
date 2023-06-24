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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {
    private TokenService service;

    @BeforeEach
    void setUp() {
        service = new TokenService();
    }

    @Test
    void generateAndGet() {
        Account account = new Account(-1);

        String token = service.generate(account);
        assertSame(account, service.get(token));
    }

    @Test
    void getNotFound() {
        assertThrows(NoSuchElementException.class, () -> service.get("not_found"));
    }

    @Test
    void getTwice() {
        String token = service.generate(new Account(-1));
        service.get(token);
        assertThrows(NoSuchElementException.class, () -> service.get("not_found"));
    }

    @Test
    void generateNotSameToken() {
        assertNotEquals(service.generate(new Account(-1)), service.generate(new Account(-1)));
    }
}
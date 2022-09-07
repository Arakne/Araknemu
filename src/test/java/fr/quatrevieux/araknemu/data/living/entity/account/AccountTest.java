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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.entity.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    void hashAndEquals() {
        Account account = new Account(15,"foo", "bar", "baz");

        assertEquals(account, account);
        assertEquals(account.hashCode(), account.hashCode());
        assertEquals(account.hashCode(), new Account(15,"foo", "bar", "baz").hashCode());

        assertNotEquals(account, new Account(14 ,"foo", "bar", "baz"));
        assertNotEquals(account.hashCode(), new Account(14 ,"foo", "bar", "baz").hashCode());
        assertNotEquals(account, new Account(15 ,"fooo", "bar", "baz"));
        assertNotEquals(account, new Account(15 ,"foo", "barr", "baz"));
        assertNotEquals(account, new Account(15 ,"foo", "bar", "bazz"));
        assertNotEquals(account, new Object());
        assertNotEquals(account, null);
    }
}

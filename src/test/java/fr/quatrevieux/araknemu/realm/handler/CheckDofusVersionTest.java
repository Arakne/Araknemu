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

package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.out.BadVersion;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckDofusVersionTest extends RealmBaseCase {
    private CheckDofusVersion handler;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CheckDofusVersion(configuration);
    }

    @Test
    void testSuccess() {
        handler.handle(
            session,
            new DofusVersion("1.29.1")
        );

        assertTrue(channel.isAlive());
        requestStack.assertEmpty();
    }

    @Test
    void badVersion() {
        handler.handle(
            session,
            new DofusVersion("1.1.5")
        );

        assertClosed();
        requestStack.assertAll(new BadVersion("1.29.1"));
    }
}

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

package fr.quatrevieux.araknemu.network.game.out.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformationMessageTest {
    private static class Impl extends InformationMessage {
        public Impl(Type type, Entry... entries) {
            super(type, entries);
        }
    }

    @Test
    void simple() {
        assertEquals(
            "Im012;",
            new Impl(InformationMessage.Type.INFO, new InformationMessage.Entry(12)).toString()
        );
    }

    @Test
    void multiple() {
        assertEquals(
            "Im05;|6;|7;",
            new Impl(
                InformationMessage.Type.INFO,
                new InformationMessage.Entry(5),
                new InformationMessage.Entry(6),
                new InformationMessage.Entry(7)
            ).toString()
        );
    }

    @Test
    void withArguments() {
        assertEquals(
            "Im05;Hello~45~World",
            new Impl(
                InformationMessage.Type.INFO,
                new InformationMessage.Entry(5, "Hello", 45, "World")
            ).toString()
        );
    }
}
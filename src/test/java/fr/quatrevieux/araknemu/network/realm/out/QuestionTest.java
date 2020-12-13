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

package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {
    @Test
    void answer() {
        String s = "my answer";
        Question a = new Question(s);

        assertSame(s, a.answer());
    }

    @Test
    void test_toString() {
        assertEquals("AQmy+answer", new Question("my answer").toString());
        assertEquals("AQ%26lt%3Bb%26gt%3Bhello%26lt%3B%2Fb%26gt%3B+%26lt%3Bi%26gt%3Bworld%26lt%3B%2Fi%26gt%3B", new Question("<b>hello</b> <i>world</i>").toString());
    }
}

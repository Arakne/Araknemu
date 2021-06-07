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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopedContainerTest {
    private ItemPoolContainer baseContainer;

    @BeforeEach
    void setUp() {
        baseContainer = new ItemPoolContainer();
        baseContainer.register(new ItemPoolContainerTest.Module());
    }

    static class NewClass implements ClassInterface, OtherInterface {}
    static class OtherClass {}
    static interface ClassInterface {}
    static interface OtherInterface {}

    @Test
    void getHas() {
        NewClass nc = new NewClass();
        OtherClass oc = new OtherClass();
        ItemPoolContainerTest.C c = new ItemPoolContainerTest.C();

        ScopedContainer scoped = ScopedContainer.fromMapping(baseContainer,
            new ScopedContainer.Mapping[] {
                new ScopedContainer.Mapping<>(nc, ClassInterface.class, OtherInterface.class),
                new ScopedContainer.Mapping<>(oc),
                new ScopedContainer.Mapping<>(c),
            }
        );

        assertTrue(scoped.has(NewClass.class));
        assertTrue(scoped.has(OtherClass.class));
        assertTrue(scoped.has(ClassInterface.class));
        assertTrue(scoped.has(OtherInterface.class));
        assertTrue(scoped.has(ItemPoolContainerTest.A.class));
        assertTrue(scoped.has(ItemPoolContainerTest.C.class));
        assertFalse(scoped.has(Object.class));

        assertSame(nc, scoped.get(NewClass.class));
        assertSame(nc, scoped.get(ClassInterface.class));
        assertSame(nc, scoped.get(OtherInterface.class));
        assertSame(oc, scoped.get(OtherClass.class));
        assertSame(c, scoped.get(ItemPoolContainerTest.C.class));
        assertSame(baseContainer.get(ItemPoolContainerTest.A.class), scoped.get(ItemPoolContainerTest.A.class));
    }

    @Test
    void register() {
        ScopedContainer scoped = ScopedContainer.fromMapping(baseContainer, new ScopedContainer.Mapping[] {});

        assertThrows(UnsupportedOperationException.class, () -> scoped.register(c -> {}));
    }
}

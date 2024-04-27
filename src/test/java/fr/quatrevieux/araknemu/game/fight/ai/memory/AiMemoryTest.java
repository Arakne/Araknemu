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

package fr.quatrevieux.araknemu.game.fight.ai.memory;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class AiMemoryTest {
    @Test
    void getDefault() {
        AiMemory memory = new AiMemory();
        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public Integer defaultValue() {
                return 42;
            }
        };

        assertEquals(42, memory.get(key));
    }

    @Test
    void getWithDefaultShouldStoreTheKey() {
        AiMemory memory = new AiMemory();
        AtomicBoolean called = new AtomicBoolean(false);
        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public Integer defaultValue() {
                return 42;
            }

            @Override
            public @Nullable Integer refresh(Integer value) {
                called.set(true);
                return value;
            }
        };

        assertEquals(42, memory.get(key));
        memory.refresh();

        assertTrue(called.get());
    }

    @Test
    void getWithoutDefaultShouldNotStoreTheKey() {
        AiMemory memory = new AiMemory();
        AtomicBoolean called = new AtomicBoolean(false);
        MemoryKey<Object> key = new MemoryKey<Object>() {
            @Override
            public Object defaultValue() {
                return null;
            }

            @Override
            public @Nullable Object refresh(Object value) {
                called.set(true);
                return value;
            }
        };

        assertNull(memory.get(key));
        memory.refresh();

        assertFalse(called.get());
    }

    @Test
    void setAndGet() {
        AiMemory memory = new AiMemory();
        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public Integer defaultValue() {
                return 42;
            }
        };

        memory.set(key, 22);
        assertEquals(22, memory.get(key));

        memory.set(key, 44);
        assertEquals(44, memory.get(key));
    }

    @Test
    void refreshKeepValue() {
        AiMemory memory = new AiMemory();
        AtomicBoolean called = new AtomicBoolean(false);
        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public @Nullable Integer refresh(Integer value) {
                called.set(true);
                return value;
            }
        };

        memory.set(key, 22);
        assertEquals(22, memory.get(key));

        memory.refresh();
        assertTrue(called.get());
        assertEquals(22, memory.get(key));
    }

    @Test
    void refreshChangeValue() {
        AiMemory memory = new AiMemory();
        AtomicBoolean called = new AtomicBoolean(false);
        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public @Nullable Integer refresh(Integer value) {
                called.set(true);
                return value * 2;
            }
        };

        memory.set(key, 22);
        assertEquals(22, memory.get(key));

        memory.refresh();
        assertTrue(called.get());
        assertEquals(44, memory.get(key));
    }

    @Test
    void refreshRemoveValue() {
        AiMemory memory = new AiMemory();
        AtomicBoolean called = new AtomicBoolean(false);
        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public @Nullable Integer refresh(Integer value) {
                called.set(true);
                return null;
            }
        };

        memory.set(key, 22);
        assertEquals(22, memory.get(key));

        memory.refresh();
        assertTrue(called.get());
        assertNull(memory.get(key));
    }
}

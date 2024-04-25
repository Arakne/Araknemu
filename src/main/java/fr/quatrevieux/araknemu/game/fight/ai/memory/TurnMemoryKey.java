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

/**
 * Memory key used to keep the value only for the current turn
 * The value will be reset at the end and the start of the turn
 *
 * @param <T> the type of the value
 */
public final class TurnMemoryKey<T> implements MemoryKey<T> {
    @Override
    public @Nullable T refresh(T value) {
        return null;
    }
}

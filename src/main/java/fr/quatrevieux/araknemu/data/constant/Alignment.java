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

package fr.quatrevieux.araknemu.data.constant;

import org.checkerframework.checker.index.qual.GTENegativeOne;

import java.util.NoSuchElementException;

/**
 * List of available alignments
 */
public enum Alignment {
    NONE,
    NEUTRAL,
    BONTARIAN,
    BRAKMARIAN,
    MERCENARY;

    private static final Alignment[] VALUES = values();

    /**
     * Get the alignment id
     */
    public int id() {
        return ordinal() - 1;
    }

    /**
     * Get an alignment by its id
     */
    public static Alignment byId(@GTENegativeOne int id) {
        final int index = id + 1;

        if (index >= VALUES.length) {
            throw new NoSuchElementException("Invalid alignment " + id);
        }

        return VALUES[index];
    }
}

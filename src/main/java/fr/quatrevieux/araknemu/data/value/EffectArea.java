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

package fr.quatrevieux.araknemu.data.value;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Value object for spell effect area
 */
public final class EffectArea {
    public enum Type {
        CELL('P'),
        CIRCLE('C'),
        CHECKERBOARD('D'),
        LINE('L'),
        CROSS('X'),
        PERPENDICULAR_LINE('T'),
        RECTANGLE('R'), // Rectangle needs 2 sizes => cannot be used on spells
        RING('O');

        private static final Map<Character, Type> typeByC = new HashMap<>();

        private final char c;

        static {
            for (Type type : values()) {
                typeByC.put(type.c, type);
            }
        }

        Type(char c) {
            this.c = c;
        }

        public char c() {
            return c;
        }

        /**
         * Get the effect area type by char id
         */
        public static Type byChar(char c) {
            if (!typeByC.containsKey(c)) {
                throw new NoSuchElementException("Invalid effect area " + c);
            }

            return typeByC.get(c);
        }
    }

    private final Type type;
    private final int size;

    public EffectArea(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public Type type() {
        return type;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return type.name() + "(" + size + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EffectArea area = (EffectArea) o;

        return
            size == area.size
            && type == area.type
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, type);
    }
}

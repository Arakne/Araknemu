package fr.quatrevieux.araknemu.data.value;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Value object for spell effect area
 */
final public class EffectArea {
    public enum Type {
        CELL('P'),
        CIRCLE('C'),
        CHECKERBOARD('D'),
        LINE('L'),
        CROSS('X'),
        PERPENDICULAR_LINE('T'),
        RECTANGLE('R'), // Rectangle needs 2 sizes => cannot be used on spells
        RING('O');

        final private char c;

        final static private Map<Character, Type> typeByC = new HashMap<>();

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
        static public Type byChar(char c) {
            if (!typeByC.containsKey(c)) {
                throw new NoSuchElementException("Invalid effect area " + c);
            }

            return typeByC.get(c);
        }
    }

    final private Type type;
    final private int size;

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

        EffectArea area = (EffectArea) o;

        return
            size == area.size
            && type == area.type
        ;
    }
}

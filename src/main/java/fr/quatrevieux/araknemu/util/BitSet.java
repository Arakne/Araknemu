package fr.quatrevieux.araknemu.util;

/**
 * Simple bit set using integer for storage
 *
 * @param <E> The set items
 */
final public class BitSet<E extends Enum> {
    private int value;


    /**
     * Set the item to the bit set
     *
     * @return true if the set has changed
     */
    public boolean set(E item) {
        int oldValue = value;

        value |= 1 << item.ordinal();

        return oldValue != value;
    }

    /**
     * Remove the item from the bit set
     *
     * @return true if the set has changed
     */
    public boolean unset(E item) {
        int oldValue = value;

        value &= ~(1 << item.ordinal());

        return oldValue != value;
    }

    /**
     * Check if the item is present in the set
     */
    public boolean check(E item) {
        final int mask = 1 << item.ordinal();

        return (value & mask) == mask;
    }

    /**
     * Get the bit set as integer value
     */
    public int toInt() {
        return value;
    }
}

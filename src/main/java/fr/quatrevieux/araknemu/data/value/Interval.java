package fr.quatrevieux.araknemu.data.value;

import java.util.Objects;

/**
 * Integer interval
 */
final public class Interval {
    final private int min;
    final private int max;

    public Interval(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    /**
     * Check if the value is contained into the interval
     * The interval is inclusive
     *
     * @param value The value to check
     *
     * @return true if value is in the interface
     */
    public boolean contains(int value) {
        return value >= min && value <= max;
    }

    /**
     * Modify the end of the interval
     * The returned interval will be [min, max + modifier]
     * If the new end is lower than the min (i.e. -modifier > max - min), the interval [min, min] will be returned
     *
     * @param modifier The modifier value. If positive will increase max, if negative will decrease
     *
     * @return The new interval
     */
    public Interval modify(int modifier)
    {
        if (modifier == 0 || (min == max && modifier < 0)) {
            return this;
        }

        int newMax = max + modifier;

        return new Interval(min, newMax < min ? min : newMax);
    }

    @Override
    public String toString() {
        return "[" + min + ", " + max + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Interval)) {
            return false;
        }

        Interval other = (Interval) obj;

        return other.min == min && other.max == max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}

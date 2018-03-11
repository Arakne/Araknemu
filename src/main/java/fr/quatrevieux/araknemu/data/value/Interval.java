package fr.quatrevieux.araknemu.data.value;

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
}

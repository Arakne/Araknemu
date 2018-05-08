package fr.quatrevieux.araknemu.util;

import java.util.Random;

/**
 * Utility for random numbers
 */
final public class RandomUtil {
    final private Random random = new Random();

    /**
     * Get random number into [min, max] interval
     * The interval is inclusive
     *
     * If max if lower than min, min is returned
     */
    public int rand(int min, int max) {
        if (max < min) {
            return min;
        }

        if (min == 0) {
            return random.nextInt(max + 1);
        }

        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Get a random number from an interval
     */
    public int rand(int[] interval) {
        if (interval.length == 1 || interval[0] > interval[1]) {
            return interval[0];
        }

        return rand(
            interval[0],
            interval[1]
        );
    }
}

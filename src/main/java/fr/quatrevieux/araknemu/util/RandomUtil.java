package fr.quatrevieux.araknemu.util;

import fr.quatrevieux.araknemu.data.value.Interval;

import java.util.*;

/**
 * Utility for random numbers
 *
 * This utility class must be used instead of the native {@link Random} class,
 * for create predictable random values on testing environment
 *
 * It's not recommended to share the RandomUtil instance between objects instances (i.e. store into a static field)
 * Each objects instance should have its own RandomUtil instance
 * But in case of short-life objects static instance of RandomUtil can be used when created using {@link RandomUtil#createShared()}
 */
final public class RandomUtil {
    /**
     * Testing mode ?
     * If set to true, the seed value will be fixed and random will be predictable
     */
    static private boolean testing = false;

    /**
     * List of instance which are shared between instances
     * This list is used for reset random seeds when enable testing mode
     *
     * @see RandomUtil#createShared()
     */
    final static private Collection<RandomUtil> sharedInstances = new ArrayList<>();

    final private Random random = create();

    /**
     * Get random number into [min, max] interval
     * The interval is inclusive
     *
     * If max if lower than min, min is returned
     */
    public int rand(int min, int max) {
        if (max <= min) {
            return min;
        }

        if (min == 0) {
            return random.nextInt(max + 1);
        }

        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Get random number into given interval
     * The interval is inclusive
     */
    public int rand(Interval interval) {
        return rand(interval.min(), interval.max());
    }

    /**
     * Get a random integer between interval [0, max[
     *
     * @param max The maximum value (exclusive)
     */
    public int integer(int max) {
        return random.nextInt(max);
    }

    /**
     * Get a random double between interval [0, max[
     *
     * @param max The maximum value (exclusive)
     */
    public double decimal(double max) {
        return random.nextDouble() * max;
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

    /**
     * Get a random boolean value
     *
     * @param percent Percent of chance that the returned value is true.
     *                If the value is 100, this method will always returns true
     *                If the value is 0, the method will always returns false
     */
    public boolean bool(int percent) {
        return random.nextInt(100) < percent;
    }

    /**
     * Get a random boolean value
     * The returned value has same chance to be true or false
     *
     * This method is equivalent to `random.bool(50)`
     *
     * @see RandomUtil#bool(int)
     */
    public boolean bool() {
        return random.nextBoolean();
    }

    /**
     * Get a random boolean value following reverse chance
     * The return value if true with the probability of 1 / rate
     * Higher the rate value is, lower the probability is
     *
     * For rate = 2, the probability is 50% and its equivalent to call random.bool(50) or random.bool()
     * For rate = 100, the probability is 1% and its equivalent to call random.bool(1)
     *
     * @param rate The rate
     *
     * @return The random boolean value
     */
    public boolean reverseBool(int rate) {
        return random.nextInt(rate) == 0;
    }

    /**
     * Get a random value from an array of values
     * All elements has the same selection probability
     *
     * @param values The provided values
     *
     * @return One of the element of the array
     */
    public<T> T of(T[] values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Get a random value from an array of characters
     *
     * @see RandomUtil#of(Object[])
     */
    public char of(char[] values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Get a random value from a list of values
     * All elements has the same selection probability
     *
     * @param values The provided values
     *
     * @return One of the element of the list
     */
    public<T> T of(List<T> values) {
        return values.get(random.nextInt(values.size()));
    }

    /**
     * Randomize list element positions
     * This method will create a copy of the list, without modify the input parameter (unlike {@link Collections#shuffle(List)})
     *
     * @param list The list to randomize
     * @param <T> The element type
     *
     * @return The randomized list
     */
    public<T> List<T> shuffle(List<T> list) {
        List<T> shuffled = new ArrayList<>(list);

        Collections.shuffle(shuffled, random);

        return shuffled;
    }

    /**
     * Create the Random instance
     * Use this method instead of `new Random()` for set predictable random during testing
     */
    static private Random create() {
        return testing ? new Random(0) : new Random();
    }

    /**
     * Create a new RandomUtil instance which is shared between instances (store in a static field)
     * This method allows the system to reset the random seed value when enable testing mode, for predictable random
     */
    static public RandomUtil createShared() {
        RandomUtil random = new RandomUtil();

        sharedInstances.add(random);

        return random;
    }

    /**
     * Enable the testing mode for get predictable random values
     * This method MUST NOT be used outside tests
     */
    static public void enableTestingMode() {
        testing = true;

        // Reset the random state
        sharedInstances.forEach(randomUtil -> randomUtil.random.setSeed(0));
    }
}

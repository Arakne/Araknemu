package fr.quatrevieux.araknemu.game.fight.turn.action.util;

/**
 * Compute the criticality rate
 */
public interface CriticalityStrategy {
    /**
     * Compute the critical hit rate
     *
     * @param base The base criticality
     *
     * @return Get the critical hit rate. The lower value is 2 (1/2). Higher the value is, lower is the critical probability
     */
    public int hitRate(int base);

    /**
     * Compute the critical failure rate
     *
     * @param base The base criticality
     *
     * @return Get the critical failure rate. The lower value is 2 (1/2).
     */
    public int failureRate(int base);

    /**
     * Random check is its a critical hit
     *
     * @param baseRate The base rate
     *
     * @return true on critical
     */
    public boolean hit(int baseRate);

    /**
     * Random check is its a critical failure
     *
     * @param baseRate The base rate
     *
     * @return true on failure
     */
    public boolean failed(int baseRate);
}

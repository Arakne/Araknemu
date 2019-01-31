package fr.quatrevieux.araknemu.game.fight.castable.effect;

/**
 * Utility class for effects
 */
final public class EffectsUtils {
    /**
     * Disable constructor
     */
    private EffectsUtils() {}

    /**
     * Check if the effect is a damage effect
     *
     * @param id The effect id
     */
    static public boolean isDamageEffect(int id) {
        return
            id == 82
            || (id >= 85 && id <= 89)
            || (id >= 91 && id <= 100)
            || id == 144
        ;
    }

    /**
     * Check if the effect results to loose action points
     *
     * @param id The effect id
     */
    static public boolean isLooseApEffect(int id) {
        return
            id == 84
            || id == 101
            || id == 168
        ;
    }
}

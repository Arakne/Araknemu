package fr.quatrevieux.araknemu.data.value;

/**
 * Effect for {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate}
 */
final public class SpellTemplateEffect {
    final private int effect;
    final private int min;
    final private int max;
    final private int special;
    final private int duration;
    final private int probability;
    final private String text;

    public SpellTemplateEffect(int effect, int min, int max, int special, int duration, int probability, String text) {
        this.effect = effect;
        this.min = min;
        this.max = max;
        this.special = special;
        this.duration = duration;
        this.probability = probability;
        this.text = text;
    }

    /**
     * Get the effect
     */
    public int effect() {
        return effect;
    }

    /**
     * Get the minimal jet value, or the first argument
     */
    public int min() {
        return min;
    }

    /**
     * Get the maximal jet value, or the first argument
     *
     * If the value is zero, the effect value is constant (min)
     */
    public int max() {
        return max;
    }

    /**
     * Get the special effect value
     * Used by invocation and boost spells
     */
    public int special() {
        return special;
    }

    /**
     * Get the effect duration
     * If this value is zero, the effect will be applied immediately
     */
    public int duration() {
        return duration;
    }

    /**
     * The effect probability in percent
     * If this value is zero, the effect will always be applied
     * For not null probability, only one effect will be choose across all "conditional" effects
     */
    public int probability() {
        return probability;
    }

    /**
     * Extra effect text. Used for dice notation
     */
    public String text() {
        return text;
    }
}

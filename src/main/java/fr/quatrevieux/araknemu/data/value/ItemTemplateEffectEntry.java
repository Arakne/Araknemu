package fr.quatrevieux.araknemu.data.value;

import fr.quatrevieux.araknemu.data.constant.Effect;

/**
 * Item effect entry
 */
final public class ItemTemplateEffectEntry {
    final private Effect effect;
    final private int min;
    final private int max;
    final private int special;
    final private String text;

    public ItemTemplateEffectEntry(Effect effect, int min, int max, int special, String text) {
        this.effect = effect;
        this.min = min;
        this.max = max;
        this.special = special;
        this.text = text;
    }

    /**
     * Get the effect
     */
    public Effect effect() {
        return effect;
    }

    /**
     * Get the minimum value
     */
    public int min() {
        return min;
    }

    /**
     * Get the max value.
     * If zero, the effect will be a constant
     */
    public int max() {
        return max;
    }

    /**
     * Get special value (not range value, reference to an external entity like spell)
     */
    public int special() {
        return special;
    }

    /**
     * Get text value
     * For basic effect, this value will be the dice (ex: 1d5+12) value
     */
    public String text() {
        return text;
    }
}

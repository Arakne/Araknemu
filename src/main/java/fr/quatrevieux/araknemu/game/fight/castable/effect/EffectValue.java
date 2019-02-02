package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Handle effect jet value
 *
 * Computed value is :
 * ( [jet + boost] * percent / 100 + fixed + effectBonus ) * multiply
 */
final public class EffectValue {
    enum State {
        MINIMIZED,
        RANDOMIZED,
        MAXIMIZED
    }

    /**
     * EffectValue is a short life object, and random is only used 1 time
     */
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    final private SpellEffect effect;

    private State state = State.RANDOMIZED;
    private int boost = 0;
    private int percent = 100;
    private int fixed = 0;
    private int multiply = 1;

    public EffectValue(SpellEffect effect) {
        this.effect = effect;
    }

    /**
     * Maximize the value
     */
    public EffectValue maximize() {
        state = State.MAXIMIZED;

        return this;
    }

    /**
     * Minimize the value
     */
    public EffectValue minimize() {
        state = State.MINIMIZED;

        return this;
    }

    /**
     * The value will be a random value between [min, max]
     */
    public EffectValue randomize() {
        state = State.RANDOMIZED;

        return this;
    }

    /**
     * Boost the dice value
     * The boost will be added at dice value
     * So the boosted value will be increased with percent
     *
     * Ex: [5, 10] + boost 5 + 50% => [15, 22]
     *
     * @param value The boosted value
     */
    public EffectValue boost(int value) {
        this.boost = value;

        return this;
    }

    /**
     * Boost with percent value
     *
     * Ex: [5, 10] + 50% => [7, 15]
     */
    public EffectValue percent(int value) {
        this.percent += value;

        return this;
    }

    /**
     * Add fixed value
     * The fixed value will be added after percent value
     *
     * Ex: [5, 10] + 5 fixed => [10, 15]
     */
    public EffectValue fixed(int value) {
        this.fixed += value;

        return this;
    }

    /**
     * Multiply the result
     * Unlike percent, the multiplier will be used at the end of the operation.
     * So, it multiplies jet, percent and fixed bonus
     */
    public EffectValue multiply(int value) {
        this.multiply = value;

        return this;
    }

    /**
     * Get the dice value
     */
    public int value() {
        int value = ((boost + jet()) * percent / 100 + fixed + effect.boost()) * multiply;

        return Math.max(value, 0);
    }

    private int jet() {
        switch (state) {
            case MINIMIZED:
                return effect.min();

            case MAXIMIZED:
                return effect.max() < effect.min() ? effect.min() : effect.max();

            case RANDOMIZED:
            default:
                return RANDOM.rand(effect.min(), effect.max());
        }
    }
}

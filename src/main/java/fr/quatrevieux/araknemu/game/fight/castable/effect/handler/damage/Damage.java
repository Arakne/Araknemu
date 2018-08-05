package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;

/**
 * Compute suffered damage
 *
 * Formula :
 * (value * percent / 100 - fixed - reduce) * multiply
 */
final public class Damage {
    final private int value;
    final private Element element;

    private int multiply = 1;
    private int fixed = 0;
    private int percent = 100;
    private int reduce = 0;

    public Damage(int value, Element element) {
        this.value = value;
        this.element = element;
    }

    /**
     * Get the damage element
     */
    public Element element() {
        return element;
    }

    /**
     * Compute the value
     */
    public int value() {
        int base = (value * percent / 100 - fixed - reduce);

        if (base <= 0) {
            return 0;
        }

        return base * multiply;
    }

    /**
     * Reduce damage in percent
     */
    public Damage percent(int percent) {
        if (percent > this.percent) {
            this.percent = 0;
        }


        this.percent -= percent;

        return this;
    }

    /**
     * Reduce fixed damage
     */
    public Damage fixed(int fixed) {
        this.fixed += fixed;

        return this;
    }

    /**
     * Multiply suffered damage
     */
    public Damage multiply(int factor) {
        this.multiply = factor;

        return this;
    }

    /**
     * Reduce fixed damage with buff effect
     */
    public Damage reduce(int value) {
        this.reduce += value;

        return this;
    }

    /**
     * Get the damage reduction value from armor buff effects
     */
    public int reducedDamage() {
        return reduce;
    }
}

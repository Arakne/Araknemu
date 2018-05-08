package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

/**
 * Compute suffered damage
 *
 * Formula :
 * (value * percent / 100 - fixed) * multiply
 */
final public class Damage {
    final private int value;
    private int multiply = 1;
    private int fixed = 0;
    private int percent = 100;

    public Damage(int value) {
        this.value = value;
    }

    /**
     * Compute the value
     */
    public int value() {
        int base = (value * percent / 100 - fixed);

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
}

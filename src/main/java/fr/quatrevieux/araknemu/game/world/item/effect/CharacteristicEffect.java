package fr.quatrevieux.araknemu.game.world.item.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

/**
 * Item effect for characteristics
 */
final public class CharacteristicEffect implements ItemEffect {
    final private Effect effect;
    final private int value;
    final private int multiplier;
    final private Characteristic characteristic;

    public CharacteristicEffect(Effect effect, int value, int multiplier, Characteristic characteristic) {
        this.effect = effect;
        this.value = value;
        this.multiplier = multiplier;
        this.characteristic = characteristic;
    }

    @Override
    public Effect effect() {
        return effect;
    }

    @Override
    public ItemTemplateEffectEntry toTemplate() {
        return new ItemTemplateEffectEntry(effect, value, 0, 0, "0d0+"+value);
    }

    /**
     * Get the effect value. This value is always positive
     *
     * @see CharacteristicEffect#boost() for the real characteristic value
     */
    public int value() {
        return value;
    }

    /**
     * Get the modified characteristic
     */
    public Characteristic characteristic() {
        return characteristic;
    }

    /**
     * Get the modifier value. This value can be negative
     *
     * @see CharacteristicEffect#value() for the effect value
     */
    public int boost() {
        return multiplier * value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        CharacteristicEffect that = (CharacteristicEffect) o;

        return value == that.value
            && effect == that.effect
        ;
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + value;

        return result;
    }

    @Override
    public String toString() {
        return "CharacteristicEffect{" + effect + ":" + value + "}";
    }
}

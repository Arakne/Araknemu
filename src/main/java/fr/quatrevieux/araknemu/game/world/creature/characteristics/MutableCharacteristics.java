package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

/**
 * Interface for mutable characteristics map
 */
public interface MutableCharacteristics extends Characteristics {
    /**
     * Set value for the given characteristic
     */
    public void set(Characteristic characteristic, int value);

    /**
     * Add the characteristic value to the characteristics map
     *
     * @param characteristic Characteristic to modify
     * @param value The value to add
     */
    public void add(Characteristic characteristic, int value);
}

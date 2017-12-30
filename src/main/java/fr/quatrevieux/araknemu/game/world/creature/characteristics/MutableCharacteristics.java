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
}

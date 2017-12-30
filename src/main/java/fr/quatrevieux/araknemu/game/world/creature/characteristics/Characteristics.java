package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

/**
 * Map for creature characteristics
 */
public interface Characteristics {
    /**
     * Get one characteristic value
     */
    public int get(Characteristic characteristic);
}

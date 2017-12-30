package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Characteristic map for player
 * This class will handle aggregation of stats, and computed stats
 */
final public class PlayerCharacteristics implements MutableCharacteristics {
    final private MutableCharacteristics base;

    public PlayerCharacteristics(MutableCharacteristics base) {
        this.base = base;
    }

    @Override
    public int get(Characteristic characteristic) {
        return base.get(characteristic);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        base.set(characteristic, value);
    }

    /**
     * Get the player base stats (i.e. boosted stats + race stats)
     */
    public Characteristics base() {
        return base;
    }

    /**
     * Get the total stuff stats
     */
    public Characteristics stuff() {
        return new DefaultCharacteristics();
    }

    /**
     * Get the feat (candy ??) stats
     */
    public Characteristics feats() {
        return new DefaultCharacteristics();
    }

    /**
     * Get the boost (buff) stats
     */
    public Characteristics boost() {
        return new DefaultCharacteristics();
    }
}

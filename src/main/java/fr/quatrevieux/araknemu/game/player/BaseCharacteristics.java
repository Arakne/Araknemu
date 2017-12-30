package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Characteristics wrapper for player base stats
 */
final public class BaseCharacteristics implements MutableCharacteristics {
    final private Characteristics raceStats;
    final private MutableCharacteristics stats;

    public BaseCharacteristics(Characteristics raceStats, MutableCharacteristics stats) {
        this.raceStats = raceStats;
        this.stats = stats;
    }

    @Override
    public int get(Characteristic characteristic) {
        return raceStats.get(characteristic) + stats.get(characteristic);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        stats.set(characteristic, value);
    }
}

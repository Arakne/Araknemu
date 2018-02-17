package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.game.world.item.type.Equipment;

/**
 * Characteristic map for player
 * This class will handle aggregation of stats, and computed stats
 *
 * @todo Remove MutableCharacteristics implements
 */
final public class PlayerCharacteristics implements MutableCharacteristics {
    final private MutableCharacteristics base;
    final private PlayerInventory inventory;
    final private Dispatcher dispatcher;

    private Characteristics stuff;

    public PlayerCharacteristics(MutableCharacteristics base, PlayerInventory inventory, Dispatcher dispatcher) {
        this.base = base;
        this.inventory = inventory;
        this.dispatcher = dispatcher;

        this.stuff = computeStuffStats();
    }

    @Override
    public int get(Characteristic characteristic) {
        return base.get(characteristic) + stuff.get(characteristic);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        base.set(characteristic, value);
    }

    @Override
    public void add(Characteristic characteristic, int value) {
        base.add(characteristic, value);
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
        return stuff;
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

    /**
     * Rebuild the stuff stats
     */
    public void rebuildStuffStats() {
        stuff = computeStuffStats();

        dispatcher.dispatch(new CharacteristicsChanged());
    }

    /**
     * Compute the stuff stats
     */
    private Characteristics computeStuffStats() {
        MutableCharacteristics characteristics = new DefaultCharacteristics();

        for (Equipment equipment : inventory.equipments()) {
            equipment.apply(characteristics);
        }

        return characteristics;
    }
}

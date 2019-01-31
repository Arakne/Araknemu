package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.type.Equipment;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All available item sets for a player
 */
final public class ItemSets {
    final private PlayerInventory inventory;

    public ItemSets(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Get player item set
     * If the item set is not found, and empty item set will be returned
     *
     * @param itemSet Item set to get
     */
    public PlayerItemSet get(GameItemSet itemSet) {
        return new PlayerItemSet(
            itemSet,
            inventory.equipments()
                .stream()
                .filter(equipment -> equipment.set().filter(set -> set.id() == itemSet.id()).isPresent())
                .map(Item::template)
                .collect(Collectors.toSet())
        );
    }

    /**
     * Get all player item sets
     */
    public Collection<PlayerItemSet> all() {
        Map<Integer, PlayerItemSet> sets = new HashMap<>();

        for (Equipment equipment : inventory.equipments()) {
            equipment.set().ifPresent(set -> {
                if (!sets.containsKey(set.id())) {
                    sets.put(set.id(), new PlayerItemSet(set));
                }

                sets.get(set.id()).add(equipment.template());
            });
        }

        return sets.values();
    }

    /**
     * Apply item set effects to player characteristics
     */
    public void apply(MutableCharacteristics characteristics) {
        for (PlayerItemSet itemSet : all()) {
            itemSet.apply(characteristics);
        }
    }

    /**
     * Apply special effects
     */
    public void applySpecials(GamePlayer player) {
        for (PlayerItemSet itemSet : all()) {
            itemSet.applySpecials(player);
        }
    }
}

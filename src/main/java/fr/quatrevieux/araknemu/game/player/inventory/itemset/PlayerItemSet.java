package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrap item set data for a player
 */
final public class PlayerItemSet {
    final private GameItemSet itemSet;
    final private Set<ItemTemplate> items;

    public PlayerItemSet(GameItemSet itemSet) {
        this(itemSet, new HashSet<>());
    }

    public PlayerItemSet(GameItemSet itemSet, Set<ItemTemplate> items) {
        this.itemSet = itemSet;
        this.items = items;
    }

    /**
     * Get the item set id
     */
    public int id() {
        return itemSet.id();
    }

    /**
     * Get the current item set bonus
     */
    public GameItemSet.Bonus bonus() {
        return itemSet.bonus(items.size());
    }

    /**
     * Get the weared item set's items
     */
    public Set<ItemTemplate> items() {
        return items;
    }

    /**
     * Check if the item set do not have items
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Add a new item to the item set
     */
    void add(ItemTemplate item) {
        items.add(item);
    }
}

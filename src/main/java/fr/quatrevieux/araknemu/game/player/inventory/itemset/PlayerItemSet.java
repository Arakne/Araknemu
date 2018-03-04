package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

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
     * Apply item set effects to player characteristics
     */
    public void apply(MutableCharacteristics characteristics) {
        for (CharacteristicEffect effect : bonus().characteristics()) {
            characteristics.add(effect.characteristic(), effect.boost());
        }
    }

    /**
     * Apply special effects
     */
    public void applySpecials(GamePlayer player) {
        for (SpecialEffect effect : bonus().specials()) {
            effect.apply(player);
        }
    }

    /**
     * Apply the current special effects bonus
     *
     * @param player The player to apply
     */
    public void applyCurrentBonus(GamePlayer player) {
        applySpecialEffectsDiff(player, items.size() - 1);
    }

    /**
     * Remove the last (next) special effect bonus
     *
     * @param player The player to apply
     */
    public void relieveLastBonus(GamePlayer player) {
        applySpecialEffectsDiff(player, items.size() + 1);
    }

    /**
     * Add a new item to the item set
     */
    void add(ItemTemplate item) {
        items.add(item);
    }

    private void applySpecialEffectsDiff(GamePlayer player, int lastNb) {
        EffectsDiff diff = new EffectsDiff(
            itemSet.bonus(lastNb).specials(),
            bonus().specials()
        );

        for (SpecialEffect effect : diff.toApply()) {
            effect.apply(player);
        }

        for (SpecialEffect effect : diff.toRelieve()) {
            effect.relieve(player);
        }
    }
}

package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Usable item
 */
final public class UsableItem implements Item {
    final private ItemTemplate template;
    final private ItemType type;
    final private List<UseEffect> useEffects;
    final private List<SpecialEffect> specials;

    public UsableItem(ItemTemplate template, ItemType type, List<UseEffect> useEffects, List<SpecialEffect> specials) {
        this.template = template;
        this.type = type;
        this.useEffects = useEffects;
        this.specials = specials;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public Optional<GameItemSet> set() {
        return Optional.empty();
    }

    @Override
    public List<? extends ItemEffect> effects() {
        List<ItemEffect> effects = new ArrayList<>(useEffects);

        effects.addAll(specials);

        return effects;
    }

    @Override
    public List<SpecialEffect> specials() {
        return specials;
    }

    @Override
    public ItemType type() {
        return type;
    }

    /**
     * Check if the player can use the item
     */
    public boolean check(ExplorationPlayer player) {
        for (UseEffect effect : useEffects) {
            if (!effect.check(player)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply the item use effects to the player
     */
    public void apply(ExplorationPlayer player) {
        for (UseEffect effect : useEffects) {
            effect.apply(player);
        }
    }

    /**
     * Check if the player can use the item to the target
     */
    public boolean checkTarget(ExplorationPlayer player, ExplorationPlayer target, int cell) {
        for (UseEffect effect : useEffects) {
            if (!effect.checkTarget(player, target, cell)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply the item use effects to the target
     */
    public void applyToTarget(ExplorationPlayer player, ExplorationPlayer target, int cell) {
        for (UseEffect effect : useEffects) {
            effect.applyToTarget(player, target, cell);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }

        UsableItem usable = (UsableItem) obj;

        return
            template.equals(usable.template)
            && useEffects.equals(usable.useEffects)
            && specials.equals(usable.specials)
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, useEffects, specials);
    }
}

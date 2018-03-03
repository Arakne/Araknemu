package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Usable item
 */
final public class UsableItem extends BaseItem {
    final private List<UseEffect> useEffects;

    public UsableItem(ItemTemplate template, List<UseEffect> useEffects, List<SpecialEffect> specials) {
        super(template, null, specials);

        this.useEffects = useEffects;
    }


    @Override
    public List<? extends ItemEffect> effects() {
        List<ItemEffect> effects = new ArrayList<>(useEffects);

        effects.addAll(super.effects());

        return effects;
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
        if (!super.equals(obj)) {
            return false;
        }

        UsableItem usable = (UsableItem) obj;

        return useEffects.equals(usable.useEffects);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + useEffects.hashCode();

        return result;
    }
}

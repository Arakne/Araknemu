package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compute special effects diff
 */
final public class EffectsDiff {
    final private List<SpecialEffect> from;
    final private List<SpecialEffect> to;

    public EffectsDiff(List<SpecialEffect> from, List<SpecialEffect> to) {
        this.from = from;
        this.to = to;
    }

    /**
     * List of effects to apply
     */
    public List<SpecialEffect> toApply() {
        if (from.isEmpty()) {
            return to;
        }

        if (to.isEmpty()) {
            return Collections.emptyList();
        }

        List<SpecialEffect> effects = new ArrayList<>();

        for (SpecialEffect effect : to) {
            if (!from.contains(effect)) {
                effects.add(effect);
            }
        }

        return effects;
    }

    /**
     * List of effects to remove
     */
    public List<SpecialEffect> toRelieve() {
        if (from.isEmpty()) {
            return Collections.emptyList();
        }

        if (to.isEmpty()) {
            return from;
        }

        List<SpecialEffect> effects = new ArrayList<>();

        for (SpecialEffect effect : from) {
            if (!to.contains(effect)) {
                effects.add(effect);
            }
        }

        return effects;
    }
}

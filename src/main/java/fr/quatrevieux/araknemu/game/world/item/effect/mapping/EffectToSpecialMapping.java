package fr.quatrevieux.araknemu.game.world.item.effect.mapping;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;

/**
 * Map item effect to special effect
 */
final public class EffectToSpecialMapping {
    /**
     * Create the special effect from template
     */
    public SpecialEffect create(ItemTemplateEffectEntry entry) {
        return new SpecialEffect(
            entry.effect(),
            new int[] {entry.min(), entry.max(), entry.special()},
            entry.text()
        );
    }
}

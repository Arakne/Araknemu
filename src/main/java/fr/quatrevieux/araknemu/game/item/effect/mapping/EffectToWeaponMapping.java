package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;

/**
 * Map item effect to weapon effect
 */
final public class EffectToWeaponMapping {
    /**
     * Create the effect from the template
     */
    public WeaponEffect create(ItemTemplateEffectEntry entry) {
        return new WeaponEffect(entry.effect(), entry.min(), entry.max(), entry.special());
    }
}

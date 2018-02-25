package fr.quatrevieux.araknemu.game.item.effect;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

/**
 * Base type for item effects
 */
public interface ItemEffect {
    /**
     * Get the effect
     */
    public Effect effect();

    /**
     * Get the template effect value
     */
    public ItemTemplateEffectEntry toTemplate();
}

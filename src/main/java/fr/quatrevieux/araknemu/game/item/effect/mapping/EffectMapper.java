package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;

import java.util.List;

/**
 * Mapper for create item effects
 *
 * @param <E> The effect type
 */
public interface EffectMapper<E extends ItemEffect> {
    /**
     * Create one item effect
     *
     * @param effect The template effect
     * @param maximize Maximize effect ?
     */
    public E create(ItemTemplateEffectEntry effect, boolean maximize);

    /**
     * Create the item effect without maximize
     *
     * @param effect The template effect
     */
    default public E create(ItemTemplateEffectEntry effect) {
        return create(effect, false);
    }

    /**
     * Create list of item effects
     *
     * @param effects The template effects
     * @param maximize Maximize effects ?
     */
    public List<E> create(List<ItemTemplateEffectEntry> effects, boolean maximize);

    /**
     *
     * Create list of item effects without maximize effects
     *
     * @param effects The template effects
     */
    default public List<E> create(List<ItemTemplateEffectEntry> effects) {
        return create(effects, false);
    }

    /**
     * Get the mapped item effect class
     */
    public Class<E> type();
}

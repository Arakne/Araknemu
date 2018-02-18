package fr.quatrevieux.araknemu.game.world.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;

/**
 * Handle special effect
 */
public interface SpecialEffectHandler {
    /**
     * Apply the effect to the player
     */
    public void apply(SpecialEffect effect, GamePlayer player);

    /**
     * Remove the effect from the player
     */
    public void relieve(SpecialEffect effect, GamePlayer player);

    /**
     * Create the Special effect
     */
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize);
}

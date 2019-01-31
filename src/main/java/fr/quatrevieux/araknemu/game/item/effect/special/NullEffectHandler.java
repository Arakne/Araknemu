package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Null object for effect handler
 */
final public class NullEffectHandler implements SpecialEffectHandler {
    final static public SpecialEffectHandler INSTANCE = new NullEffectHandler();

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        // No op
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        // No op
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        return new SpecialEffect(
            this,
            entry.effect(),
            new int[] {entry.min(), entry.max(), entry.special()},
            entry.text()
        );
    }
}

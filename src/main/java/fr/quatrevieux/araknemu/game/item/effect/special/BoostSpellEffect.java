package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Apply spell boost
 */
final public class BoostSpellEffect implements SpecialEffectHandler {
    final private SpellsBoosts.Modifier modifier;

    public BoostSpellEffect(SpellsBoosts.Modifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        player.spells().boosts().boost(
            effect.arguments()[0],
            modifier,
            effect.arguments()[2]
        );
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        player.spells().boosts().boost(
            effect.arguments()[0],
            modifier,
            -effect.arguments()[2]
        );
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

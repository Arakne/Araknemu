package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Set fixed value for spell modifier
 */
final public class SetSpellModifierEffect implements SpecialEffectHandler {
    final private SpellsBoosts.Modifier modifier;

    public SetSpellModifierEffect(SpellsBoosts.Modifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        final SpellsBoosts boosts = player.properties().spells().boosts();

        int spell = effect.arguments()[0];
        int value = effect.arguments()[2];

        if (!boosts.modifiers(spell).has(modifier) || boosts.modifiers(spell).value(modifier) > value) {
            boosts.set(spell, modifier, value);
        }
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        final SpellsBoosts boosts = player.properties().spells().boosts();

        int spell = effect.arguments()[0];
        int value = effect.arguments()[2];

        if (boosts.modifiers(spell).value(modifier) == value) {
            boosts.unset(spell, modifier);
        }
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

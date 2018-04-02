package fr.quatrevieux.araknemu.game.spell.boost;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Collection;

/**
 * SpellsBoosts with dispatcher
 */
final public class DispatcherSpellsBoosts implements SpellsBoosts {
    final private SpellsBoosts boosts;
    final private Dispatcher dispatcher;

    public DispatcherSpellsBoosts(SpellsBoosts boosts, Dispatcher dispatcher) {
        this.boosts = boosts;
        this.dispatcher = dispatcher;
    }

    @Override
    public int boost(int spellId, Modifier modifier, int value) {
        int newValue = boosts.boost(spellId, modifier, value);
        dispatcher.dispatch(new SpellBoostChanged(spellId, modifier, newValue));

        return newValue;
    }

    @Override
    public int set(int spellId, Modifier modifier, int value) {
        boosts.set(spellId, modifier, value);
        dispatcher.dispatch(new SpellBoostChanged(spellId, modifier, value));

        return value;
    }

    @Override
    public void unset(int spellId, Modifier modifier) {
        boosts.unset(spellId, modifier);
        dispatcher.dispatch(new SpellBoostChanged(spellId, modifier, -1));
    }

    @Override
    public SpellModifiers modifiers(int spellId) {
        return boosts.modifiers(spellId);
    }

    @Override
    public Spell get(Spell spell) {
        return boosts.get(spell);
    }

    @Override
    public Collection<SpellModifiers> all() {
        return boosts.all();
    }
}

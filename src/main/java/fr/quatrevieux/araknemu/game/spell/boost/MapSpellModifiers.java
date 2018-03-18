package fr.quatrevieux.araknemu.game.spell.boost;

import java.util.Map;

/**
 * Implements {@link SpellModifiers} using map
 */
final public class MapSpellModifiers implements SpellModifiers {
    final private int spellId;
    final private Map<SpellsBoosts.Modifier, Integer> modifiers;

    public MapSpellModifiers(int spellId, Map<SpellsBoosts.Modifier, Integer> modifiers) {
        this.spellId = spellId;
        this.modifiers = modifiers;
    }

    @Override
    public int spellId() {
        return spellId;
    }

    @Override
    public int value(SpellsBoosts.Modifier modifier) {
        return modifiers.getOrDefault(modifier, 0);
    }

    @Override
    public boolean has(SpellsBoosts.Modifier modifier) {
        return modifiers.containsKey(modifier);
    }
}

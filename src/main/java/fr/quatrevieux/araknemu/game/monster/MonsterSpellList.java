package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellList;

import java.util.Map;

/**
 * Simple spell list for monster.
 *
 * This list is immutable, and spells cannot be upgraded nor boosted.
 * Because of its properties, this list can be shared between monster instances without conflicts.
 */
final public class MonsterSpellList implements SpellList {
    final private Map<Integer, Spell> spells;

    public MonsterSpellList(Map<Integer, Spell> spells) {
        this.spells = spells;
    }

    @Override
    public Spell get(int spellId) {
        return spells.get(spellId);
    }

    @Override
    public boolean has(int spellId) {
        return spells.containsKey(spellId);
    }
}

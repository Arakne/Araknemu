package fr.quatrevieux.araknemu.game.spell.boost;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.boost.spell.BoostedSpell;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple implementation of spell boosts
 */
final public class SimpleSpellsBoosts implements SpellsBoosts {
    final private Map<Integer, Map<Modifier, Integer>> spellsModifiers = new HashMap<>();

    @Override
    public int boost(int spellId, Modifier modifier, int value) {
        if (
            !spellsModifiers.containsKey(spellId)
            || !spellsModifiers.get(spellId).containsKey(modifier)
        ) {
            return set(spellId, modifier, value);
        }

        Map<Modifier, Integer> modifiers = spellsModifiers.get(spellId);

        int newValue = modifiers.get(modifier) + value;
        modifiers.put(modifier, newValue);

        return newValue;
    }

    @Override
    public int set(int spellId, Modifier modifier, int value) {
        if (!spellsModifiers.containsKey(spellId)) {
            spellsModifiers.put(spellId, new EnumMap<>(Modifier.class));
        }

        spellsModifiers.get(spellId).put(modifier, value);

        return value;
    }

    @Override
    public void unset(int spellId, Modifier modifier) {
        if (!spellsModifiers.containsKey(spellId)) {
            return;
        }

        spellsModifiers.get(spellId).remove(modifier);
    }

    @Override
    public SpellModifiers modifiers(int spellId) {
        return new MapSpellModifiers(
            spellId,
            spellsModifiers.getOrDefault(spellId, Collections.emptyMap())
        );
    }

    @Override
    public Spell get(Spell spell) {
        if (!spellsModifiers.containsKey(spell.id())) {
            return spell;
        }

        return new BoostedSpell(spell, modifiers(spell.id()));
    }

    @Override
    public Collection<SpellModifiers> all() {
        return spellsModifiers.entrySet()
            .stream()
            .map(entry -> new MapSpellModifiers(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList())
        ;
    }
}

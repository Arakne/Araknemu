package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle launch spells history for check constraints
 */
final public class LaunchedSpells {
    private class Entry {
        private int cooldown;
        private int count = 1;
        final private Map<Fighter, Integer> countPerTarget = new HashMap<>();

        Entry(Spell spell, FightCell cell) {
            cooldown = spell.constraints().launchDelay();

            cell.fighter().ifPresent(fighter -> countPerTarget.put(fighter, 1));
        }
    }

    final private Map<Integer, Entry> spells = new HashMap<>();

    /**
     * Decrement cooldown and refresh the list
     */
    public void refresh() {
        spells.entrySet().removeIf(entry -> --entry.getValue().cooldown <= 0);
    }

    /**
     * Push a new spell into the list
     *
     * @param spell The launched spell
     * @param target The target
     */
    public void push(Spell spell, FightCell target) {
        if (!spells.containsKey(spell.id())) {
            spells.put(spell.id(), new Entry(spell, target));
            return;
        }

        Entry entry = spells.get(spell.id());

        ++entry.count;
        target.fighter().ifPresent(
            fighter -> entry.countPerTarget.put(
                fighter,
                entry.countPerTarget.getOrDefault(fighter, 0) + 1
            )
        );
    }

    /**
     * Check if the spell can be casted according to the launch constraints
     *
     * @param spell Spell to check
     * @param target The cast target
     *
     * @return true is the spell can be launched
     */
    public boolean valid(Spell spell, FightCell target) {
        if (!spells.containsKey(spell.id())) {
            return true;
        }

        Entry entry = spells.get(spell.id());

        if (entry.cooldown > 0) {
            return false;
        }

        if (spell.constraints().launchPerTurn() > 0 && entry.count >= spell.constraints().launchPerTurn()) {
            return false;
        }

        if (
            spell.constraints().launchPerTarget() > 0
            && target.fighter().isPresent()
            && entry.countPerTarget.containsKey(target.fighter().get())
            && entry.countPerTarget.get(target.fighter().get()) >= spell.constraints().launchPerTarget()
        ) {
            return false;
        }

        return true;
    }
}

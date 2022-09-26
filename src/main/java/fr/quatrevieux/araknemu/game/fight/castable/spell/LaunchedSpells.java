/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle launch spells history for check constraints
 */
public final class LaunchedSpells {
    private final Map<Integer, Entry> spells = new HashMap<>();

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
        final Entry entry = spells.get(spell.id());

        if (entry == null) {
            spells.put(spell.id(), new Entry(spell, target));
            return;
        }

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
        final Entry entry = spells.get(spell.id());

        if (entry == null) {
            return true;
        }

        if (entry.cooldown > 0) {
            return false;
        }

        if (spell.constraints().launchPerTurn() > 0 && entry.count >= spell.constraints().launchPerTurn()) {
            return false;
        }

        return checkPerTarget(spell, entry, target);
    }

    private boolean checkPerTarget(Spell spell, Entry entry, FightCell target) {
        if (spell.constraints().launchPerTarget() <= 0 || !target.fighter().isPresent()) {
            return true;
        }

        final Integer perTarget = entry.countPerTarget.get(target.fighter().get());

        return perTarget == null || perTarget < spell.constraints().launchPerTarget();
    }

    private static class Entry {
        private int cooldown;
        private int count = 1;
        private final Map<FighterData, Integer> countPerTarget = new HashMap<>();

        Entry(Spell spell, FightCell cell) {
            cooldown = spell.constraints().launchDelay();

            cell.fighter().ifPresent(fighter -> countPerTarget.put(fighter, 1));
        }
    }
}

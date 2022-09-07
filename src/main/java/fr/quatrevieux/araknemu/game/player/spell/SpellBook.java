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

package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellLearned;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.spell.boost.DispatcherSpellsBoosts;
import fr.quatrevieux.araknemu.game.spell.boost.SimpleSpellsBoosts;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.EnsuresKeyForIf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The player spell book
 */
public final class SpellBook implements SpellList, Dispatcher {
    public static final int MAX_POSITION = 24;

    private final Dispatcher dispatcher;
    private final Player player;

    private final Map<Integer, SpellBookEntry> entries = new HashMap<>();
    private final @Nullable SpellBookEntry[] entriesByPosition = new SpellBookEntry[MAX_POSITION];
    private final SpellsBoosts boosts;

    @SuppressWarnings("argument")
    public SpellBook(Dispatcher dispatcher, Player player) {
        this.dispatcher = dispatcher;
        this.player = player;

        this.boosts = new DispatcherSpellsBoosts(new SimpleSpellsBoosts(), dispatcher);
    }

    @Override
    public Spell get(int spellId) {
        return boosts.get(entry(spellId).spell());
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    @Override
    public Iterator<Spell> iterator() {
        return entries.values().stream()
            .map(SpellBookEntry::spell)
            .filter(spell -> spell.minPlayerLevel() <= player.level())
            .iterator()
        ;
    }

    /**
     * Get all available spells
     */
    public Collection<SpellBookEntry> all() {
        return entries.values();
    }

    /**
     * Get one spell entry
     *
     * @param spellId The spell id
     */
    public SpellBookEntry entry(int spellId) {
        if (!has(spellId)) {
            throw new NoSuchElementException("The spell book do not contains Spell " + spellId);
        }

        return entries.get(spellId);
    }

    /**
     * Check if the user has the spell
     *
     * @param spellId Spell to check
     */
    @Override
    @EnsuresKeyForIf(result = true, expression = "#1", map = "entries")
    @SuppressWarnings("contracts.conditional.postcondition") // checker do not consider null check as key existence
    public boolean has(int spellId) {
        final SpellBookEntry entry = entries.get(spellId);

        return entry != null && entry.spell().minPlayerLevel() <= player.level();
    }

    /**
     * Check if the player can learn the spell
     *
     * @param spell Spell to learn
     */
    public boolean canLearn(SpellLevels spell) {
        return !has(spell.id()) && player.level() >= spell.level(1).minPlayerLevel();
    }

    /**
     * Learn a spell
     */
    public void learn(SpellLevels spell) {
        if (!canLearn(spell)) {
            throw new IllegalArgumentException("Cannot learn the spell " + spell.name() + " (" + spell.id() + ")");
        }

        final SpellBookEntry entry = new SpellBookEntry(
            this,
            new PlayerSpell(player.id(), spell.id(), false),
            spell
        );

        entries.put(spell.id(), entry);
        dispatch(new SpellLearned(entry));
    }

    /**
     * Get available spell upgrade points
     */
    public @NonNegative int upgradePoints() {
        return player.spellPoints();
    }

    /**
     * Get the spells boosts
     */
    public SpellsBoosts boosts() {
        return boosts;
    }

    /**
     * Check if the entry already exists on the spell book
     * Unlike {@link SpellBook#has(int)} the level of the player is not checked
     *
     * /!\ Internal method /!\
     */
    boolean hasEntry(int spellId) {
        return entries.containsKey(spellId);
    }

    /**
     * Add an entry to the spell book
     *
     * /!\ Internal method /!\
     */
    void addEntry(PlayerSpell entity, SpellLevels spell) {
        final SpellBookEntry entry = new SpellBookEntry(this, entity, spell);

        entries.put(spell.id(), entry);
        indexing(entry);
    }

    void freePosition(SpellBookEntry entry) {
        if (entry.position() > MAX_POSITION) {
            return;
        }

        entriesByPosition[entry.position() - 1] = null;
    }

    void indexing(SpellBookEntry entry) {
        final int position = entry.position() - 1;

        if (position >= MAX_POSITION) {
            return;
        }

        final SpellBookEntry lastEntry = entriesByPosition[position];

        if (lastEntry != null) {
            lastEntry.move(PlayerSpell.DEFAULT_POSITION);
        }

        entriesByPosition[position] = entry;
    }

    @SuppressWarnings("argument") // canUpgrade() is called before, so spell points cannot be < 0
    void removePointsForUpgrade(Spell spell) {
        player.setSpellPoints(player.spellPoints() - spell.level() + 1);
    }

    boolean canUpgrade(Spell spell) {
        return player.spellPoints() >= spell.level() - 1
            && player.level() >= spell.minPlayerLevel()
        ;
    }
}

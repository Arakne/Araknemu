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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellLearned;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.game.spell.boost.spell.BoostedSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class SpellBookTest extends GameBaseCase {
    private SpellService service;
    private Player player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = container.get(SpellService.class);
        dataSet.pushSpells();

        player = dataSet.createPlayer(1);
    }

    @Test
    void all() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertContainsAll(book.all(), entries.toArray());
    }

    @Test
    void entryNotFound() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertThrows(NoSuchElementException.class, () -> book.entry(789));
    }

    @Test
    void entry() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertSame(entries.get(0), book.entry(3));
    }

    @Test
    void has() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertTrue(book.has(3));
        assertTrue(book.has(6));
        assertFalse(book.has(123));
    }

    @Test
    void hasTooHighLevel() throws SQLException {
        dataSet.pushHighLevelSpells();

        List<SpellBookEntry> entries = Collections.singletonList(
            new SpellBookEntry(new PlayerSpell(1, 1908, true, 1, 1), service.get(1908))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertFalse(book.has(1908));
    }

    @Test
    void learnSuccess() throws NoSuchFieldException, IllegalAccessException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        SpellBook book = new SpellBook(dispatcher, player, new ArrayList<>());

        AtomicReference<SpellLearned> ref = new AtomicReference<>();
        dispatcher.add(SpellLearned.class, ref::set);

        book.learn(service.get(3));

        assertCount(1, book.all());
        assertEquals(1, book.entry(3).spell().level());
        assertEquals(63, book.entry(3).position());
        assertFalse(book.entry(3).classSpell());

        assertEquals(player.id(), book.entry(3).entity().playerId());
        assertEquals(3, book.entry(3).entity().spellId());
        assertEquals(1, book.entry(3).entity().level());

        assertSame(book.entry(3), ref.get().entry());

        // Issue : https://github.com/Arakne/Araknemu/issues/151
        Field field = SpellBookEntry.class.getDeclaredField("spellBook");
        field.setAccessible(true);

        assertSame(book, field.get(book.entry(3)));
    }

    @Test
    void learnFailed() throws SQLException, ContainerException {
        dataSet.pushHighLevelSpells();

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        SpellBook book = new SpellBook(dispatcher, player, new ArrayList<>());

        AtomicReference<SpellLearned> ref = new AtomicReference<>();
        dispatcher.add(SpellLearned.class, ref::set);

        assertThrows(IllegalArgumentException.class, () -> book.learn(service.get(1908)), "Cannot learn the spell Invocation de Dopeul Iop (1908)");
        assertNull(ref.get());
        assertFalse(book.has(1908));
    }

    @Test
    void canLearnTooLowLevel() throws SQLException, ContainerException {
        dataSet.pushHighLevelSpells();

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, new ArrayList<>());

        assertFalse(book.canLearn(service.get(1908)));
    }

    @Test
    void canLearnAlreadyLearned() throws SQLException, ContainerException {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertFalse(book.canLearn(service.get(3)));
    }

    @Test
    void canUpgradeTooHighLevel() {
        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, new ArrayList<>());
        player.setLevel(5);
        player.setSpellPoints(10);

        Spell spell = Mockito.mock(Spell.class);
        Mockito.when(spell.minPlayerLevel()).thenReturn(6);
        Mockito.when(spell.level()).thenReturn(1);

        assertFalse(book.canUpgrade(spell));
    }

    @Test
    void canUpgradeNoEnoughPoints() {
        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, new ArrayList<>());
        player.setLevel(5);
        player.setSpellPoints(2);

        Spell spell = Mockito.mock(Spell.class);
        Mockito.when(spell.minPlayerLevel()).thenReturn(1);
        Mockito.when(spell.level()).thenReturn(4);

        assertFalse(book.canUpgrade(spell));
    }

    @Test
    void canUpgradeExactlyGoodPoints() {
        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, new ArrayList<>());
        player.setLevel(5);
        player.setSpellPoints(4);

        Spell spell = Mockito.mock(Spell.class);
        Mockito.when(spell.minPlayerLevel()).thenReturn(1);
        Mockito.when(spell.level()).thenReturn(5);

        assertTrue(book.canUpgrade(spell));
    }

    @Test
    void removePointsForUpgrade() {
        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, new ArrayList<>());
        player.setSpellPoints(5);

        Spell spell = Mockito.mock(Spell.class);
        Mockito.when(spell.level()).thenReturn(5);

        book.removePointsForUpgrade(spell);

        assertEquals(1, book.upgradePoints());
    }

    @Test
    void getNotBoosted() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertSame(book.get(3), book.entry(3).spell());
    }

    @Test
    void getBoosted() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);
        book.boosts().boost(3, SpellsBoosts.Modifier.DAMAGE, 50);
        book.boosts().boost(3, SpellsBoosts.Modifier.AP_COST, 2);

        assertInstanceOf(BoostedSpell.class, book.get(3));
        assertEquals(7, book.get(3).effects().get(0).min());
        assertEquals(11, book.get(3).effects().get(0).max());
        assertEquals(50, book.get(3).effects().get(0).boost());
        assertEquals(2, book.get(3).apCost());
    }

    @Test
    void iterator() throws SQLException {
        dataSet.pushHighLevelSpells();

        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 1), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 2), service.get(6)),
            new SpellBookEntry(new PlayerSpell(1, 1908, true, 1, 63), service.get(1908))
        );

        SpellBook book = new SpellBook(new DefaultListenerAggregate(), player, entries);

        assertArrayEquals(
            new int[] {3, 6},
            StreamSupport.stream(book.spliterator(), true)
                .mapToInt(Spell::id)
                .toArray()
        );
    }
}

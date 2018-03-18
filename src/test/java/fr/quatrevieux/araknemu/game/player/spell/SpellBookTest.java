package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.spell.SpellLearned;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.game.spell.boost.spell.BoostedSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

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
    void learnSuccess() {
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
        assertEquals(57, book.get(3).effects().get(0).min());
        assertEquals(61, book.get(3).effects().get(0).max());
        assertEquals(2, book.get(3).apCost());
    }
}

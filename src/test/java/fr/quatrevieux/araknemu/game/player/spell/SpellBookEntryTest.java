package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.spell.SpellMoved;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellMove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SpellBookEntryTest extends GameBaseCase {
    private SpellService service;
    private SpellBookEntry entry;
    private SpellBook book;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushSpells()
        ;

        service = container.get(SpellService.class);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 3, true, 5, 63),
            service.get(3)
        );

        book = new SpellBook(
            dispatcher = new DefaultListenerAggregate(),
            dataSet.createPlayer(1),
            Arrays.asList(
                entry,
                new SpellBookEntry(
                    new PlayerSpell(1, 6, true, 5, 5),
                    service.get(6)
                )
            )
        );
    }

    @Test
    void getters() {
        assertEquals(3, entry.spell().id());
        assertEquals(5, entry.spell().level());
        assertEquals(63, entry.position());
        assertTrue(entry.classSpell());
    }

    @Test
    void moveFromDefaultToFreePosition() {
        AtomicReference<SpellMoved> ref = new AtomicReference<>();
        dispatcher.add(SpellMoved.class, ref::set);

        entry.move(2);

        assertEquals(2, entry.position());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void moveFromPositionToDefault() {
        entry.move(2);

        AtomicReference<SpellMoved> ref = new AtomicReference<>();
        dispatcher.add(SpellMoved.class, ref::set);

        entry.move(63);

        assertEquals(63, entry.position());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void moveFromDefaultToUsedPosition() {
        List<SpellMoved> events = new ArrayList<>();

        dispatcher.add(SpellMoved.class, events::add);

        entry.move(5);

        assertEquals(5, entry.position());
        assertEquals(63, book.entry(6).position());

        assertSame(book.entry(6), events.get(0).entry());
        assertSame(entry, events.get(1).entry());
    }

    @Test
    void moveFromPositionToUsedPosition() {
        entry.move(2);

        List<SpellMoved> events = new ArrayList<>();

        dispatcher.add(SpellMoved.class, events::add);

        entry.move(5);

        assertEquals(5, entry.position());
        assertEquals(63, book.entry(6).position());

        assertSame(book.entry(6), events.get(0).entry());
        assertSame(entry, events.get(1).entry());
    }

    @Test
    void moveBadPosition() {
        assertThrows(IllegalArgumentException.class, () -> entry.move(0));
    }

    @Test
    void attachAlreadyAttached() {
        assertThrows(IllegalStateException.class, () -> entry.attach(new SpellBook(null, null, new ArrayList<>())));
    }
}

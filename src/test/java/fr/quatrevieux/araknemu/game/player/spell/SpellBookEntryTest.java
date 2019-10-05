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

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellMoved;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Test
    void upgradeTooLowLevel() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(100);

        assertThrows(IllegalStateException.class, () -> entry.upgrade(), "Cannot upgrade spell");
    }

    @Test
    void upgradeMaxLevel() {
        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 5, 63),
            service.get(202)
        ).attach(book);

        assertThrows(IllegalStateException.class, () -> entry.upgrade(), "Maximum spell level reached");
    }

    @Test
    void upgradeNotEnoughPoints() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(0);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false),
            service.get(202)
        ).attach(book);

        assertThrows(IllegalStateException.class, () -> entry.upgrade(), "Cannot upgrade spell");
    }

    @Test
    void upgradeSuccessWillDispatchEvent() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(10);

        AtomicReference<SpellUpgraded> ref = new AtomicReference<>();
        dispatcher.add(SpellUpgraded.class, ref::set);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false),
            service.get(202)
        ).attach(book);

        entry.upgrade();

        assertSame(entry, ref.get().entry());
    }

    @Test
    void upgradeToLevel2() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(10);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 1, 63),
            service.get(202)
        ).attach(book);

        entry.upgrade();

        assertEquals(2, entry.spell().level());
        assertEquals(9, book.upgradePoints());
    }

    @Test
    void upgradeToLevel3() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(10);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 2, 63),
            service.get(202)
        ).attach(book);

        entry.upgrade();

        assertEquals(3, entry.spell().level());
        assertEquals(8, book.upgradePoints());
    }

    @Test
    void upgradeToLevel4() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(10);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 3, 63),
            service.get(202)
        ).attach(book);

        entry.upgrade();

        assertEquals(4, entry.spell().level());
        assertEquals(7, book.upgradePoints());
    }

    @Test
    void upgradeToLevel5() throws NoSuchFieldException, IllegalAccessException {
        this.<Player>readField(book, "player").setSpellPoints(10);

        entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 4, 63),
            service.get(202)
        ).attach(book);

        entry.upgrade();

        assertEquals(5, entry.spell().level());
        assertEquals(6, book.upgradePoints());
    }
}

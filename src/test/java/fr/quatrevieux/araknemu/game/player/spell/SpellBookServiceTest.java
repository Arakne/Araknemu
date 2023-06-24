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
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.listener.player.spell.SaveLearnedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SaveSpellPosition;
import fr.quatrevieux.araknemu.game.listener.player.spell.SaveUpgradedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendAllSpellBoosts;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendLearnedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendSpellBoost;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendSpellList;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendUpgradedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SetDefaultPositionSpellBook;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellBookServiceTest extends GameBaseCase {
    private SpellBookService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushRaces()
            .pushSpells()
            .use(PlayerSpell.class)
        ;

        service = new SpellBookService(
            container.get(PlayerSpellRepository.class),
            container.get(SpellService.class),
            container.get(PlayerRaceService.class)
        );
    }

    @Test
    void listeners() {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        assertTrue(dispatcher.has(SetDefaultPositionSpellBook.class));
    }

    @Test
    void playerLoadedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(SendSpellList.class));
        assertTrue(gamePlayer().dispatcher().has(SaveSpellPosition.class));
        assertTrue(gamePlayer().dispatcher().has(SaveLearnedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SendLearnedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SaveUpgradedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SendUpgradedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SendSpellBoost.class));
        assertTrue(gamePlayer().dispatcher().has(SendAllSpellBoosts.class));
    }

    @Test
    void loadWithOnlyClassSpell() throws ContainerException {
        Player player = dataSet.pushPlayer("Robert", 1, 2);

        SpellBook spells = service.load(new DefaultListenerAggregate(), player);

        List<SpellBookEntry> entries = new ArrayList<>(spells.all());

        assertCount(3, entries);
        assertEquals(17, entries.get(0).spell().id());
        assertEquals(1, entries.get(0).spell().level());
        assertTrue(entries.get(0).classSpell());
        assertEquals(63, entries.get(0).position());
        assertEquals(3, entries.get(1).spell().id());
        assertTrue(entries.get(1).classSpell());
        assertEquals(1, entries.get(1).spell().level());
        assertEquals(63, entries.get(1).position());
        assertEquals(6, entries.get(2).spell().id());
        assertTrue(entries.get(1).classSpell());
        assertEquals(1, entries.get(2).spell().level());
        assertEquals(63, entries.get(2).position());
    }

    @Test
    void loadWithClassSpellUpdated() throws ContainerException {
        Player player = dataSet.pushPlayer("Robert", 1, 2);

        dataSet.push(new PlayerSpell(player.id(), 3, true, 5, 1));
        dataSet.push(new PlayerSpell(player.id(), 6, true, 2, 2));
        dataSet.push(new PlayerSpell(player.id(), 17, true, 3, 3));

        SpellBook spells = service.load(new DefaultListenerAggregate(), player);

        List<SpellBookEntry> entries = new ArrayList<>(spells.all());

        assertCount(3, entries);

        assertEquals(17, entries.get(0).spell().id());
        assertEquals(3, entries.get(0).spell().level());
        assertEquals(3, entries.get(0).position());

        assertEquals(3, entries.get(1).spell().id());
        assertEquals(5, entries.get(1).spell().level());
        assertEquals(1, entries.get(1).position());

        assertEquals(6, entries.get(2).spell().id());
        assertEquals(2, entries.get(2).spell().level());
        assertEquals(2, entries.get(2).position());
    }

    @Test
    void loadWithLearnedSpell() throws ContainerException {
        Player player = dataSet.pushPlayer("Robert", 1, 2);

        dataSet.push(new PlayerSpell(player.id(), 202, false));

        SpellBook spells = service.load(new DefaultListenerAggregate(), player);

        List<SpellBookEntry> entries = new ArrayList<>(spells.all());

        assertCount(4, entries);

        assertEquals(17, entries.get(0).spell().id());
        assertEquals(3, entries.get(1).spell().id());
        assertEquals(6, entries.get(2).spell().id());
        assertEquals(202, entries.get(3).spell().id());
        assertFalse(entries.get(3).classSpell());
    }
}

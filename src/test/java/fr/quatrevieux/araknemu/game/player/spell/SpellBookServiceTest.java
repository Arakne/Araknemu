package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddSpellListeners;
import fr.quatrevieux.araknemu.game.event.listener.service.SetDefaultPositionSpellBook;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
            container.get(PlayerRaceService.class),
            container.get(ListenerAggregate.class)
        );
    }

    @Test
    void preload() throws ContainerException {
        service.preload(NOPLogger.NOP_LOGGER);

        assertTrue(container.get(ListenerAggregate.class).has(AddSpellListeners.class));
        assertTrue(container.get(ListenerAggregate.class).has(SetDefaultPositionSpellBook.class));
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

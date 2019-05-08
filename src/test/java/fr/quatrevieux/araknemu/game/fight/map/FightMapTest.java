package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class FightMapTest extends GameBaseCase {
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = new FightMap(
            container.get(MapTemplateRepository.class).get(10340)
        );
    }

    @Test
    void getters() {
        assertEquals(10340, map.id());
    }

    @Test
    void iterator() {
        Iterator<FightCell> it = map.iterator();

        assertSame(map.get(0), it.next());
        assertSame(map.get(1), it.next());
        assertSame(map.get(2), it.next());
    }

    @Test
    void getWalkable() {
        FightCell cell = map.get(123);

        assertSame(cell, map.get(123));
        assertInstanceOf(WalkableFightCell.class, cell);
        assertTrue(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertEquals(123, cell.id());
        assertSame(map, cell.map());
    }

    @Test
    void getNotWalkable() {
        FightCell cell = map.get(1);

        assertSame(cell, map.get(1));
        assertInstanceOf(UnwalkableFightCell.class, cell);
        assertFalse(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertEquals(1, cell.id());
        assertSame(map, cell.map());
    }

    @Test
    void startPlaces() throws ContainerException {
        assertEquals(container.get(MapTemplateRepository.class).get(10340).fightPlaces()[0], map.startPlaces(0));
        assertEquals(container.get(MapTemplateRepository.class).get(10340).fightPlaces()[1], map.startPlaces(1));
    }

    @Test
    void values() {
        assertEquals(17, map.dimensions().height());
        assertEquals(15, map.dimensions().width());
        assertEquals(479, map.size());
    }

    @Test
    void destroy() {
        map.destroy();

        assertEquals(0, map.size());
    }
}

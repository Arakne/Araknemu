package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void getWalkable() {
        FightCell cell = map.get(123);

        assertSame(cell, map.get(123));
        assertInstanceOf(WalkableFightCell.class, cell);
        assertTrue(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertEquals(123, cell.id());
    }

    @Test
    void getNotWalkable() {
        FightCell cell = map.get(1);

        assertSame(cell, map.get(1));
        assertInstanceOf(UnwalkableFightCell.class, cell);
        assertFalse(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertEquals(1, cell.id());
    }

    @Test
    void startPlaces() throws ContainerException {
        assertEquals(container.get(MapTemplateRepository.class).get(10340).fightPlaces()[0], map.startPlaces(0));
        assertEquals(container.get(MapTemplateRepository.class).get(10340).fightPlaces()[1], map.startPlaces(1));
    }
}
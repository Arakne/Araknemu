package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicCellTest extends GameBaseCase {
    private MapTemplateRepository repository;
    private ExplorationMapService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        repository = container.get(MapTemplateRepository.class);
        service = container.get(ExplorationMapService.class);
    }

    @Test
    void map() {
        ExplorationMap map = service.load(10340);

        BasicCell cell = new BasicCell(185, repository.get(10340).cells().get(185), map);

        assertSame(map, cell.map());
    }

    @Test
    void walkableCell() {
        BasicCell cell = new BasicCell(185, repository.get(10340).cells().get(185), service.load(10340));

        assertEquals(185, cell.id());
        assertTrue(cell.walkable());
    }

    @Test
    void deactivatedCell() {
        BasicCell cell = new BasicCell(0, repository.get(10340).cells().get(0), service.load(10340));

        assertEquals(0, cell.id());
        assertFalse(cell.walkable());
    }

    @Test
    void unwalkableCell() {
        BasicCell cell = new BasicCell(209, repository.get(10340).cells().get(209), service.load(10340));

        assertEquals(209, cell.id());
        assertFalse(cell.walkable());
    }

    @Test
    void equals() {
        BasicCell cell = new BasicCell(185, repository.get(10340).cells().get(185), service.load(10340));

        assertEquals(cell, cell);
        assertEquals(cell, new BasicCell(185, repository.get(10340).cells().get(185), service.load(10340)));
        assertNotEquals(cell, new BasicCell(180, repository.get(10340).cells().get(180), service.load(10340)));
        assertNotEquals(cell, new Object());
    }
}

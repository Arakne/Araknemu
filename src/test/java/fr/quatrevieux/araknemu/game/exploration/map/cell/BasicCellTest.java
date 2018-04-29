package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicCellTest extends GameBaseCase {
    private MapTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        repository = container.get(MapTemplateRepository.class);
    }

    @Test
    void walkableCell() {
        BasicCell cell = new BasicCell(185, repository.get(10340).cells().get(185));

        assertEquals(185, cell.id());
        assertTrue(cell.walkable());
    }

    @Test
    void deactivatedCell() {
        BasicCell cell = new BasicCell(0, repository.get(10340).cells().get(0));

        assertEquals(0, cell.id());
        assertFalse(cell.walkable());
    }

    @Test
    void unwalkableCell() {
        BasicCell cell = new BasicCell(209, repository.get(10340).cells().get(209));

        assertEquals(209, cell.id());
        assertFalse(cell.walkable());
    }

    @Test
    void equals() {
        BasicCell cell = new BasicCell(185, repository.get(10340).cells().get(185));

        assertEquals(cell, cell);
        assertEquals(cell, new BasicCell(185, repository.get(10340).cells().get(185)));
        assertNotEquals(cell, new BasicCell(180, repository.get(10340).cells().get(180)));
        assertNotEquals(cell, new Object());
    }
}

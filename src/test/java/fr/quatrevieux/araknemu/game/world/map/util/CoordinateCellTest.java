package fr.quatrevieux.araknemu.game.world.map.util;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateCellTest extends GameBaseCase {
    private GameMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = new FightMap(container.get(MapTemplateRepository.class).get(10340));
    }

    @Test
    void firstCell() {
        CoordinateCell cell = new CoordinateCell(map.get(0));

        assertEquals(0, cell.x());
        assertEquals(0, cell.y());
    }

    @Test
    void cell() {
        CoordinateCell cell = new CoordinateCell(map.get(157));

        assertEquals(17, cell.x());
        assertEquals(-7, cell.y());
    }

    @Test
    void yAxis() {
        int inc = Direction.SOUTH_EAST.nextCellIncrement(map.dimensions().width());

        for (int i = 0, x = 0; i + inc <= 420; i += inc, ++x) {
            CoordinateCell cell = new CoordinateCell(map.get(i));

            assertEquals(0, cell.y());
            assertEquals(x, cell.x());
        }
    }

    @Test
    void xAxis() {
        int inc = Direction.SOUTH_WEST.nextCellIncrement(map.dimensions().width());

        for (int i = 123, y = -3; i + inc <= 305; i += inc, ++y) {
            CoordinateCell cell = new CoordinateCell(map.get(i));

            assertEquals(11, cell.x());
            assertEquals(y, cell.y());
        }
    }

    @Test
    void directionTo() {
        CoordinateCell cell = new CoordinateCell(map.get(157));

        assertEquals(Direction.SOUTH_WEST, cell.directionTo(new CoordinateCell(map.get(227))));
        assertEquals(Direction.NORTH_EAST, cell.directionTo(new CoordinateCell(map.get(129))));
        assertEquals(Direction.SOUTH_EAST, cell.directionTo(new CoordinateCell(map.get(217))));
        assertEquals(Direction.NORTH_WEST, cell.directionTo(new CoordinateCell(map.get(67))));
    }

    @Test
    void distance() {
        CoordinateCell cell = new CoordinateCell(map.get(157));

        assertEquals(0, cell.distance(cell));
        assertEquals(5, cell.distance(new CoordinateCell(map.get(227))));
        assertEquals(24, cell.distance(new CoordinateCell(map.get(0))));
    }
}

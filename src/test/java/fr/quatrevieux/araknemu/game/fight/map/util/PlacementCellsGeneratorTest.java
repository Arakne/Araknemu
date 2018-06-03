package fr.quatrevieux.araknemu.game.fight.map.util;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlacementCellsGeneratorTest extends GameBaseCase {
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        map = container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340));
    }

    @Test
    void nextSimple() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(123, 124, 125));

        assertEquals(123, generator.next().id());
        assertEquals(124, generator.next().id());
        assertEquals(125, generator.next().id());
    }

    @Test
    void nextWithNonFreeCells() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(123, 124, 125));

        map.get(123).set(Mockito.mock(Fighter.class));

        assertEquals(124, generator.next().id());
        assertEquals(125, generator.next().id());
    }

    @Test
    void nextWithNonAvailableCells() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, new ArrayList<>());

        for (int i = 0; i < 100; ++i) {
            FightCell cell = generator.next();
            assertTrue(cell.walkable());
        }
    }

    @Test
    void nextWithAllAvailableCellsNotFree() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(123, 124, 125));

        map.get(123).set(Mockito.mock(Fighter.class));
        map.get(124).set(Mockito.mock(Fighter.class));
        map.get(125).set(Mockito.mock(Fighter.class));

        FightCell cell = generator.next();

        assertTrue(cell.walkable());
        assertNotEquals(123, cell.id());
        assertNotEquals(124, cell.id());
        assertNotEquals(125, cell.id());
    }
}

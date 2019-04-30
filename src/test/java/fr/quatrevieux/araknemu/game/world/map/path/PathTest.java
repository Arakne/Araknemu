package fr.quatrevieux.araknemu.game.world.map.path;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PathTest extends GameBaseCase {
    private Decoder<MapCell> decoder;
    private GameMap map;
    private Path<MapCell> path;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10300);

        decoder = new Decoder<>(map);
        path = new Path<>(
            decoder,
            Arrays.asList(
                new PathStep(map.get(100), Direction.EAST),
                new PathStep(map.get(99), Direction.WEST),
                new PathStep(map.get(98), Direction.WEST),
                new PathStep(map.get(69), Direction.NORTH)
            )
        );
    }

    @Test
    void encode() {
        assertEquals("abKebIgbf", path.encode());
    }

    @Test
    void first() {
        assertSame(path.get(0), path.first());
    }

    @Test
    void last() {
        assertSame(path.get(3), path.last());
    }

    @Test
    void start() {
        assertEquals(map.get(100), path.start());
    }

    @Test
    void target() {
        assertEquals(map.get(69), path.target());
    }

    @Test
    void size() {
        assertEquals(4, path.size());
    }

    @Test
    void keepWhile() {
        Path newPath = path.keepWhile(pathStep -> pathStep.direction() != Direction.NORTH);

        assertEquals(3, newPath.size());
        assertEquals(path.get(0), newPath.get(0));
        assertEquals(path.get(1), newPath.get(1));
        assertEquals(path.get(2), newPath.get(2));
    }

    @Test
    void truncate() {
        Path newPath = path.truncate(2);

        assertEquals(2, newPath.size());
        assertEquals(path.get(0), newPath.get(0));
        assertEquals(path.get(1), newPath.get(1));

        assertSame(path, path.truncate(100));
    }
}

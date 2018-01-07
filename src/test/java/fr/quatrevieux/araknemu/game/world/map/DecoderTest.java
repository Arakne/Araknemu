package fr.quatrevieux.araknemu.game.world.map;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecoderTest extends GameBaseCase {
    private Decoder decoder;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        decoder = new Decoder(
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void nextCellByDirection() {
        assertEquals(101, decoder.nextCellByDirection(100, Direction.EAST));
        assertEquals(115, decoder.nextCellByDirection(100, Direction.SOUTH_EAST));
        assertEquals(129, decoder.nextCellByDirection(100, Direction.SOUTH));
        assertEquals(114, decoder.nextCellByDirection(100, Direction.SOUTH_WEST));
        assertEquals(99, decoder.nextCellByDirection(100, Direction.WEST));
        assertEquals(85, decoder.nextCellByDirection(100, Direction.NORTH_WEST));
        assertEquals(71, decoder.nextCellByDirection(100, Direction.NORTH));
        assertEquals(86, decoder.nextCellByDirection(100, Direction.NORTH_EAST));
    }

    @Test
    void decodePathInvalidBadLength() {
        assertThrows(PathException.class, () -> decoder.decodePath("abcd", 123), "Invalid path : bad length");
    }

    @Test
    void decodePathBadDirection() {
        assertThrows(PathException.class, () -> decoder.decodePath("aaJ", 100), "Invalid path : bad direction");
    }

    @Test
    void decodePathCellNotFound() {
        assertThrows(PathException.class, () -> decoder.decodePath("aZZ", 100), "Invalid cell number");
    }

    @Test
    void decodePathOneCell() throws PathException {
        List<PathStep> path = decoder.decodePath("ebJ", 100);

        assertEquals(2, path.size());
        assertEquals(100, path.get(0).cell());
        assertEquals(Direction.WEST, path.get(1).direction());
        assertEquals(99, path.get(1).cell());
    }

    @Test
    void decodePathRectilinear() throws PathException {
        List<PathStep> path = decoder.decodePath("ebH", 100);

        assertEquals(4, path.size());
        assertEquals(100, path.get(0).cell());
        assertEquals(99, path.get(1).cell());
        assertEquals(98, path.get(2).cell());
        assertEquals(97, path.get(3).cell());
    }

    @Test
    void encodePathOneCell() {
        assertEquals(
            "abKebJ",
            decoder.encodePath(Arrays.asList(
                new PathStep(100, Direction.EAST),
                new PathStep(99, Direction.WEST)
            ))
        );
    }

    @Test
    void encodeRectilinearPathWillCompress() {
        assertEquals(
            "abKebH",
            decoder.encodePath(Arrays.asList(
                new PathStep(100, Direction.EAST),
                new PathStep(99, Direction.WEST),
                new PathStep(98, Direction.WEST),
                new PathStep(97, Direction.WEST)
            ))
        );
    }

    @Test
    void encodeComplexPath() {
        assertEquals(
            "abKebIgbf",
            decoder.encodePath(Arrays.asList(
                new PathStep(100, Direction.EAST),
                new PathStep(99, Direction.WEST),
                new PathStep(98, Direction.WEST),
                new PathStep(69, Direction.NORTH)
            ))
        );
    }

    @Test
    void decodeComplex() throws PathException {
        List<PathStep> path = decoder.decodePath("ebIgbf", 100);

        assertEquals(4, path.size());
        assertEquals(100, path.get(0).cell());
        assertEquals(99, path.get(1).cell());
        assertEquals(98, path.get(2).cell());
        assertEquals(69, path.get(3).cell());
    }
}

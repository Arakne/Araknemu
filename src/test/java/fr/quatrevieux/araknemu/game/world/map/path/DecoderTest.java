package fr.quatrevieux.araknemu.game.world.map.path;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecoderTest extends GameBaseCase {
    private Decoder decoder;
    private GameMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10300);

        decoder = new Decoder(map);
    }

    @Test
    void nextCellByDirection() throws PathException {
        assertEquals(map.get(101), decoder.nextCellByDirection(map.get(100), Direction.EAST));
        assertEquals(map.get(115), decoder.nextCellByDirection(map.get(100), Direction.SOUTH_EAST));
        assertEquals(map.get(129), decoder.nextCellByDirection(map.get(100), Direction.SOUTH));
        assertEquals(map.get(114), decoder.nextCellByDirection(map.get(100), Direction.SOUTH_WEST));
        assertEquals(map.get(99), decoder.nextCellByDirection(map.get(100), Direction.WEST));
        assertEquals(map.get(85), decoder.nextCellByDirection(map.get(100), Direction.NORTH_WEST));
        assertEquals(map.get(71), decoder.nextCellByDirection(map.get(100), Direction.NORTH));
        assertEquals(map.get(86), decoder.nextCellByDirection(map.get(100), Direction.NORTH_EAST));
    }

    @Test
    void nextCellByDirectionOutOfLimit() {
        assertThrows(PathException.class, () -> decoder.nextCellByDirection(map.get(470), Direction.SOUTH));
    }

    @Test
    void decodePathInvalidBadLength() {
        assertThrows(PathException.class, () -> decoder.decode("abcd", map.get(123)), "Invalid path : bad length");
    }

    @Test
    void decodePathBadDirection() {
        assertThrows(PathException.class, () -> decoder.decode("aaJ", map.get(100)), "Invalid path : bad direction");
    }

    @Test
    void decodePathCellNotFound() {
        assertThrows(PathException.class, () -> decoder.decode("aZZ", map.get(100)), "Invalid cell number");
    }

    @Test
    void decodePathOneCell() throws PathException {
        Path path = decoder.decode("ebJ", map.get(100));

        assertEquals(2, path.size());
        assertEquals(map.get(100), path.get(0).cell());
        assertEquals(Direction.WEST, path.get(1).direction());
        assertEquals(map.get(99), path.get(1).cell());
    }

    @Test
    void decodePathRectilinear() throws PathException {
        Path path = decoder.decode("ebH", map.get(100));

        assertEquals(4, path.size());
        assertEquals(map.get(100), path.get(0).cell());
        assertEquals(map.get(99), path.get(1).cell());
        assertEquals(map.get(98), path.get(2).cell());
        assertEquals(map.get(97), path.get(3).cell());
    }

    @Test
    void encodePathOneCell() {
        assertEquals(
            "abKebJ",
            decoder.encode(
                new Path(
                    decoder,
                    Arrays.asList(
                        new PathStep(map.get(100), Direction.EAST),
                        new PathStep(map.get(99), Direction.WEST)
                    )
                )
            )
        );
    }

    @Test
    void encodeRectilinearPathWillCompress() {
        assertEquals(
            "abKebH",
            decoder.encode(
                new Path(
                    decoder,
                    Arrays.asList(
                        new PathStep(map.get(100), Direction.EAST),
                        new PathStep(map.get(99), Direction.WEST),
                        new PathStep(map.get(98), Direction.WEST),
                        new PathStep(map.get(97), Direction.WEST)
                    )
                )
            )
        );
    }

    @Test
    void encodeComplexPath() {
        assertEquals(
            "abKebIgbf",
            decoder.encode(
                new Path(
                    decoder,
                    Arrays.asList(
                        new PathStep(map.get(100), Direction.EAST),
                        new PathStep(map.get(99), Direction.WEST),
                        new PathStep(map.get(98), Direction.WEST),
                        new PathStep(map.get(69), Direction.NORTH)
                    )
                )
            )
        );
    }

    /**
     * https://github.com/vincent4vx/Araknemu/issues/69
     */
    @Test
    void encodeRectilinearPathWillNotCompressTheStartCellOnEast() {
        assertEquals(
            "abKabN",
            decoder.encode(
                new Path(
                    decoder,
                    Arrays.asList(
                        new PathStep(map.get(100), Direction.EAST),
                        new PathStep(map.get(101), Direction.EAST),
                        new PathStep(map.get(102), Direction.EAST),
                        new PathStep(map.get(103), Direction.EAST)
                    )
                )
            )
        );
    }

    @Test
    void decodeComplex() throws PathException {
        Path path = decoder.decode("ebIgbf", map.get(100));

        assertEquals(4, path.size());
        assertEquals(map.get(100), path.get(0).cell());
        assertEquals(map.get(99), path.get(1).cell());
        assertEquals(map.get(98), path.get(2).cell());
        assertEquals(map.get(69), path.get(3).cell());
    }
}

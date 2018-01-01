package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapCellTransformerTest {
    private MapCellTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MapCellTransformer();
    }

    @Test
    void unserializeSameDataWillReturnSameObject() {
        assertSame(
            transformer.unserialize("Hhaaeaaaaa"),
            transformer.unserialize("Hhaaeaaaaa")
        );
    }

    @Test
    void unserializeEmptyCell() {
        MapTemplate.Cell cell = transformer.unserialize("Hhaaeaaaaa");

        assertTrue(cell.LineOfSight());
        assertFalse(cell.interactive());
        assertEquals(0, cell.objectId());
        assertEquals(0, cell.movement());
    }

    @Test
    void unserializeNotEmptyCell() {
        MapTemplate.Cell cell = transformer.unserialize("GhhceaaaWt");

        assertFalse(cell.LineOfSight());
        assertEquals(0, cell.movement());
        assertFalse(cell.interactive());
        assertEquals(3091, cell.objectId());
    }
}

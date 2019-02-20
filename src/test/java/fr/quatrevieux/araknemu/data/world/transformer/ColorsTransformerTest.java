package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.data.value.Colors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorsTransformerTest {
    private ColorsTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ColorsTransformer();
    }

    @Test
    void serialize() {
        assertEquals("7b,1c8,315", transformer.serialize(new Colors(123, 456, 789)));
    }

    @Test
    void unserializeSimple() {
        Colors colors = transformer.unserialize("7b,1c8,315");

        assertEquals(123, colors.color1());
        assertEquals(456, colors.color2());
        assertEquals(789, colors.color3());
    }

    @Test
    void unserializeDefault() {
        assertSame(Colors.DEFAULT, transformer.unserialize("-1,-1,-1"));
    }

    @Test
    void unserializeWithOneDefault() {
        Colors colors = transformer.unserialize("-1,1c8,315");

        assertEquals(-1, colors.color1());
        assertEquals(456, colors.color2());
        assertEquals(789, colors.color3());
    }

    @Test
    void unserializeInvalid() {
        assertThrows(TransformerException.class, () -> transformer.unserialize("-1,1c8;456"));
    }
}

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectAreaTransformerTest {
    private EffectAreaTransformer transformer = new EffectAreaTransformer();

    @Test
    void unserializeNull() {
        assertNull(transformer.unserialize(null));
    }

    @Test
    void unserialize() {
        assertEquals(new EffectArea(EffectArea.Type.CIRCLE, 3), transformer.unserialize("Cd"));
    }

    @Test
    void serializeNull() {
        assertNull(transformer.serialize(null));
    }

    @Test
    void serialize() {
        assertEquals("Cd", transformer.serialize(new EffectArea(EffectArea.Type.CIRCLE, 3)));
    }
}

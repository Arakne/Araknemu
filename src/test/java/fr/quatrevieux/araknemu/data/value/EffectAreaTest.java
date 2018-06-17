package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectAreaTest {
    @Test
    void equalsSameObjects() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertTrue(area.equals(area));
    }

    @Test
    void equalsNull() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertFalse(area.equals(null));
    }

    @Test
    void equals() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertFalse(area.equals(new EffectArea(EffectArea.Type.RING, 3)));
        assertFalse(area.equals(new EffectArea(EffectArea.Type.CIRCLE, 4)));
        assertTrue(area.equals(new EffectArea(EffectArea.Type.CIRCLE, 3)));
    }
}

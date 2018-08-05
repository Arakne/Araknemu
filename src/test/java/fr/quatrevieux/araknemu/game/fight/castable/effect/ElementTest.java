package fr.quatrevieux.araknemu.game.fight.castable.effect;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ElementTest {
    @Test
    void fromBitSet() {
        assertEquals(Collections.emptySet(), Element.fromBitSet(0));
        assertEquals(EnumSet.of(Element.NEUTRAL), Element.fromBitSet(1));
        assertEquals(EnumSet.of(Element.EARTH), Element.fromBitSet(2));
        assertEquals(EnumSet.of(Element.WATER), Element.fromBitSet(4));
        assertEquals(EnumSet.of(Element.AIR), Element.fromBitSet(8));
        assertEquals(EnumSet.of(Element.FIRE), Element.fromBitSet(16));
        assertEquals(EnumSet.of(Element.AIR, Element.NEUTRAL), Element.fromBitSet(9));
        assertSame(Element.fromBitSet(9), Element.fromBitSet(9));
    }
}
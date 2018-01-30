package fr.quatrevieux.araknemu.data.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlignmentTest {
    @Test
    void byId() {
        assertEquals(Alignment.NONE, Alignment.byId(-1));
        assertEquals(Alignment.NEUTRAL, Alignment.byId(0));
        assertEquals(Alignment.BONTARIAN, Alignment.byId(1));
        assertEquals(Alignment.BRAKMARIAN, Alignment.byId(2));
        assertEquals(Alignment.MERCENARY, Alignment.byId(3));
    }
}

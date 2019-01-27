package fr.quatrevieux.araknemu.network.game.out.dialog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogLeavedTest {
    @Test
    void generate() {
        assertEquals("DV", new DialogLeaved().toString());
    }
}
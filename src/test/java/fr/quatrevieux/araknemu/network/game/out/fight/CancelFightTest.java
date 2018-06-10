package fr.quatrevieux.araknemu.network.game.out.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancelFightTest {
    @Test
    void generate() {
        assertEquals("GV", new CancelFight().toString());
    }
}
package fr.quatrevieux.araknemu.network.game.out.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeginFightTest {
    @Test
    void generate() {
        assertEquals("GS", new BeginFight().toString());
    }
}
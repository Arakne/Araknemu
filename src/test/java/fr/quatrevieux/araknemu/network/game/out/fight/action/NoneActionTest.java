package fr.quatrevieux.araknemu.network.game.out.fight.action;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoneActionTest {
    @Test
    void generate() {
        assertEquals("GA;0", new NoneAction().toString());
    }
}
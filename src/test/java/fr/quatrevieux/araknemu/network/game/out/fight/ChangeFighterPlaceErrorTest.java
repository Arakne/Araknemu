package fr.quatrevieux.araknemu.network.game.out.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeFighterPlaceErrorTest {
    @Test
    void generate() {
        assertEquals("GICe", new ChangeFighterPlaceError().toString());
    }
}
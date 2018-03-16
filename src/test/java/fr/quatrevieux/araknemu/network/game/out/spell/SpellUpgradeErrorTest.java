package fr.quatrevieux.araknemu.network.game.out.spell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellUpgradeErrorTest {
    @Test
    void generate() {
        assertEquals("SUE", new SpellUpgradeError().toString());
    }
}
package fr.quatrevieux.araknemu.network.game.in.spell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellUpgradeTest {
    @Test
    void parse() {
        assertEquals(
            123,
            new SpellUpgrade.Parser().parse("123").spellId()
        );
    }
}

package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellBoostTest {
    @Test
    void generate() {
        assertEquals(
            "SB287;12;25",
            new SpellBoost(12, SpellsBoosts.Modifier.CRITICAL, 25).toString()
        );
    }
}

package fr.quatrevieux.araknemu.network.game.in.spell;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellMoveTest extends GameBaseCase {
    @Test
    void parse() {
        SpellMove move = new SpellMove.Parser().parse("123|5");

        assertEquals(123, move.spellId());
        assertEquals(5, move.position());
    }
}

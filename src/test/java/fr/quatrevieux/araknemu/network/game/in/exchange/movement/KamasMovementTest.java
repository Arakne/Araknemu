package fr.quatrevieux.araknemu.network.game.in.exchange.movement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KamasMovementTest {
    @Test
    void parse() {
        assertEquals(123, new KamasMovement.Parser().parse("123").quantity());
    }
}

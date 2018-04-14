package fr.quatrevieux.araknemu.game.fight.type;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTypeTest {
    @Test
    void values() {
        ChallengeType type = new ChallengeType();

        assertEquals(0, type.id());
        assertFalse(type.hasPlacementTimeLimit());
        assertTrue(type.canCancel());
        assertEquals(Duration.ofSeconds(30), type.turnDuration());
    }
}
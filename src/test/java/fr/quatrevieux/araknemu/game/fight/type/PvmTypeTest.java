package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.ChallengeRewardsGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class PvmTypeTest extends TestCase {
    @Test
    void values() {
        PvmType type = new PvmType();

        assertEquals(4, type.id());
        assertTrue(type.hasPlacementTimeLimit());
        assertFalse(type.canCancel());
        assertEquals(Duration.ofSeconds(30), type.turnDuration());
        assertEquals(45, type.placementTime());
        assertInstanceOf(ChallengeRewardsGenerator.class, type.rewards());
    }
}
package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.ChallengeRewardsGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTypeTest extends TestCase {
    @Test
    void values() {
        ChallengeType type = new ChallengeType();

        assertEquals(0, type.id());
        assertFalse(type.hasPlacementTimeLimit());
        assertTrue(type.canCancel());
        assertEquals(Duration.ofSeconds(30), type.turnDuration());
        assertInstanceOf(ChallengeRewardsGenerator.class, type.rewards());
    }
}
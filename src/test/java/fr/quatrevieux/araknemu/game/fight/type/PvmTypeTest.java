package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.ChallengeRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.PvmRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.RewardsGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PvmTypeTest extends TestCase {
    @Test
    void values() {
        RewardsGenerator generator = new PvmRewardsGenerator(Collections.emptyList(), Collections.emptyList());
        PvmType type = new PvmType(generator);

        assertEquals(4, type.id());
        assertTrue(type.hasPlacementTimeLimit());
        assertFalse(type.canCancel());
        assertEquals(Duration.ofSeconds(30), type.turnDuration());
        assertEquals(45, type.placementTime());
        assertSame(generator, type.rewards());
    }
}
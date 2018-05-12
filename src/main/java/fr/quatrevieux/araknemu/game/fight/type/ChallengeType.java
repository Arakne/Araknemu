package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.ChallengeRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.RewardsGenerator;

import java.time.Duration;

/**
 * Fight type for challenge
 */
final public class ChallengeType implements FightType {
    @Override
    public int id() {
        return 0;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean hasPlacementTimeLimit() {
        return false;
    }

    @Override
    public int placementTime() {
        return 0;
    }

    @Override
    public Duration turnDuration() {
        return Duration.ofSeconds(30); // @todo configuration
    }

    @Override
    public RewardsGenerator rewards() {
        return new ChallengeRewardsGenerator();
    }
}

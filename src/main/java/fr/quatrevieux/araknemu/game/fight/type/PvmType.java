package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.ChallengeRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.RewardsGenerator;

import java.time.Duration;

/**
 * Fight type for PvM
 */
final public class PvmType implements FightType {
    @Override
    public int id() {
        return 4;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public boolean hasPlacementTimeLimit() {
        return true;
    }

    @Override
    public int placementTime() {
        return 45; // @todo configuration
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

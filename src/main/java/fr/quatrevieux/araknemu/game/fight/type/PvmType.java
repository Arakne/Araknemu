package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;

import java.time.Duration;

/**
 * Fight type for PvM
 */
final public class PvmType implements FightType {
    final private RewardsGenerator rewardsGenerator;

    public PvmType(RewardsGenerator rewardsGenerator) {
        this.rewardsGenerator = rewardsGenerator;
    }

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
        return rewardsGenerator;
    }
}

package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;

import java.util.Optional;

/**
 * The fighter has leave the fight
 */
final public class FightLeaved {
    final private FightReward reward;

    public FightLeaved() {
        this(null);
    }

    public FightLeaved(FightReward reward) {
        this.reward = reward;
    }

    public Optional<FightReward> reward() {
        return Optional.ofNullable(reward);
    }
}

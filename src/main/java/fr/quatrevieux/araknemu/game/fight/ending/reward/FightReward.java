package fr.quatrevieux.araknemu.game.fight.ending.reward;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Reward at end of fight
 */
public interface FightReward {
    /**
     * The fighter
     */
    public Fighter fighter();

    /**
     * Get the reward line type
     */
    public RewardType type();

    /**
     * Apply reward visitor to the current reward
     */
    public void apply(FightRewardVisitor visitor);
}

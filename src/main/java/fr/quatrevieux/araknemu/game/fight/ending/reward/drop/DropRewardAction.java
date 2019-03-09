package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Reward action for drop type
 */
public interface DropRewardAction {
    /**
     * Apply the reward to the fighter
     */
    public void apply(DropReward reward, Fighter fighter);
}

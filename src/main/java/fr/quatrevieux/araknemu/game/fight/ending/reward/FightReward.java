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
     * Apply the reward to the fighter
     */
    public void apply();

    /**
     * Render the reward line for {@link fr.quatrevieux.araknemu.network.game.out.fight.FightEnd} packet
     */
    public String render();
}

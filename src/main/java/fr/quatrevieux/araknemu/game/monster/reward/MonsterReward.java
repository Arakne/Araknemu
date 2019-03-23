package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;

/**
 * Store rewards for a single monster grade (real winning rewards)
 */
public interface MonsterReward {
    /**
     * Winning kamas interval
     */
    public Interval kamas();

    /**
     * Winning base experience
     */
    public long experience();
}

package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;

import java.util.List;

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

    /**
     * List of dropped items
     */
    public List<MonsterRewardItem> items();
}

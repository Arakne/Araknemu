package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;

import java.util.Collections;
import java.util.List;

/**
 * Null object for rewards
 */
final public class NullMonsterGradesReward implements MonsterGradesReward {
    final private Interval NULL_INTERVAL = new Interval(0, 0);
    final private MonsterReward NULL_REWARD = new MonsterReward() {
        @Override
        public Interval kamas() {
            return NULL_INTERVAL;
        }

        @Override
        public long experience() {
            return 0;
        }

        @Override
        public List<MonsterRewardItem> items() {
            return Collections.emptyList();
        }
    };

    final static public NullMonsterGradesReward INSTANCE = new NullMonsterGradesReward();

    @Override
    public Interval kamas() {
        return NULL_INTERVAL;
    }

    @Override
    public long experience(int gradeNumber) {
        return 0;
    }

    @Override
    public List<MonsterRewardItem> items() {
        return Collections.emptyList();
    }

    @Override
    public MonsterReward grade(int gradeNumber) {
        return NULL_REWARD;
    }
}

package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;

/**
 * Base implementation for grade set rewards
 */
final public class DefaultMonsterGradesReward implements MonsterGradesReward {
    final private MonsterRewardData data;

    public DefaultMonsterGradesReward(MonsterRewardData data) {
        this.data = data;
    }

    @Override
    public Interval kamas() {
        return data.kamas();
    }

    @Override
    public long experience(int gradeNumber) {
        return data.experiences()[gradeNumber - 1];
    }

    @Override
    public MonsterReward grade(int gradeNumber) {
        return new DefaultMonsterReward(this, gradeNumber);
    }
}

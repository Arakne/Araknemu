package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage all monster rewards
 */
final public class MonsterRewardService implements PreloadableService {
    final private MonsterRewardRepository repository;

    final private Map<Integer, MonsterGradesReward> rewards = new ConcurrentHashMap<>();

    public MonsterRewardService(MonsterRewardRepository repository) {
        this.repository = repository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading monsters rewards...");

        for (MonsterRewardData data : repository.all()) {
            rewards.put(data.id(), new DefaultMonsterGradesReward(data));
        }

        logger.info("{} monsters rewards loaded", rewards.size());
    }

    /**
     * Get the rewards for the given monster grade
     *
     * @param monsterId The monster id to load
     * @param gradeNumber The grade number (starts at 1)
     */
    public MonsterReward get(int monsterId, int gradeNumber) {
        return rewards
            .computeIfAbsent(monsterId, id -> repository.get(id)
                .<MonsterGradesReward>map(DefaultMonsterGradesReward::new)
                .orElse(NullMonsterGradesReward.INSTANCE)
            )
            .grade(gradeNumber)
        ;
    }
}

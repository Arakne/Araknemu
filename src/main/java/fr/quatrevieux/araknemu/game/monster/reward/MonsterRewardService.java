package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardItemRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage all monster rewards
 */
final public class MonsterRewardService implements PreloadableService {
    final private MonsterRewardRepository repository;
    final private MonsterRewardItemRepository itemRepository;

    final private Map<Integer, MonsterGradesReward> rewards = new ConcurrentHashMap<>();

    public MonsterRewardService(MonsterRewardRepository repository, MonsterRewardItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading monsters rewards...");

        Map<Integer, List<MonsterRewardItem>> itemDrops = itemRepository.all();

        for (MonsterRewardData data : repository.all()) {
            rewards.put(
                data.id(),
                new DefaultMonsterGradesReward(
                    data,
                    itemDrops.getOrDefault(data.id(), Collections.emptyList())
                )
            );
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
                .<MonsterGradesReward>map(data -> new DefaultMonsterGradesReward(data, itemRepository.byMonster(monsterId)))
                .orElse(NullMonsterGradesReward.INSTANCE)
            )
            .grade(gradeNumber)
        ;
    }
}

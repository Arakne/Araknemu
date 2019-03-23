package fr.quatrevieux.araknemu.data.world.repository.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link MonsterRewardData}
 */
public interface MonsterRewardRepository extends Repository<MonsterRewardData> {
    /**
     * Get reward for one monster by the monster id
     */
    public Optional<MonsterRewardData> get(int id);

    /**
     * Load all monster rewards
     */
    public List<MonsterRewardData> all();
}

package fr.quatrevieux.araknemu.data.world.repository.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;

import java.util.List;
import java.util.Map;

/**
 * Repository for {@link fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem}
 */
public interface MonsterRewardItemRepository extends Repository<MonsterRewardItem> {
    /**
     * Load item drop for a monster
     *
     * If the monster cannot be found, an empty list is returned
     *
     * @param monsterId The monster id {@link MonsterRewardItem#monsterId()}
     */
    public List<MonsterRewardItem> byMonster(int monsterId);

    /**
     * Load all monster item drop, indexed by the monster id
     */
    public Map<Integer, List<MonsterRewardItem>> all();
}

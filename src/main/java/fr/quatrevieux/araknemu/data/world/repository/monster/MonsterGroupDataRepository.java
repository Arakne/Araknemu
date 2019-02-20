package fr.quatrevieux.araknemu.data.world.repository.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;

import java.util.List;

/**
 * Repository for {@link MonsterGroupData}
 */
public interface MonsterGroupDataRepository extends Repository<MonsterGroupData> {
    /**
     * Get one monster group by its id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When the template do not exists
     */
    public MonsterGroupData get(int id);

    /**
     * Load all monster templates
     */
    public List<MonsterGroupData> all();
}

package fr.quatrevieux.araknemu.data.world.repository.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;

import java.util.List;

/**
 * Repository for {@link MonsterTemplate}
 */
public interface MonsterTemplateRepository extends Repository<MonsterTemplate> {
    /**
     * Get one monster template by its id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When the template do not exists
     */
    public MonsterTemplate get(int id);

    /**
     * Load all monster templates
     */
    public List<MonsterTemplate> all();
}

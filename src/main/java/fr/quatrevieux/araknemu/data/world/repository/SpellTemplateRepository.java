package fr.quatrevieux.araknemu.data.world.repository;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;

import java.util.Collection;

/**
 * Repository for {@link SpellTemplate}
 */
public interface SpellTemplateRepository extends Repository<SpellTemplate> {
    /**
     * Get a spell by its id
     *
     * @param id The spell id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the spell
     */
    public SpellTemplate get(int id);

    /**
     * Load all spells
     */
    public Collection<SpellTemplate> load();
}

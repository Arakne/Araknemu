package fr.quatrevieux.araknemu.data.world.repository.environment;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;

import java.util.Collection;

/**
 * Repository for load maps
 */
public interface MapTemplateRepository extends Repository<MapTemplate> {
    /**
     * Get a map by its id
     */
    public MapTemplate get(int id);

    /**
     * Get all map templates
     */
    public Collection<MapTemplate> all();
}

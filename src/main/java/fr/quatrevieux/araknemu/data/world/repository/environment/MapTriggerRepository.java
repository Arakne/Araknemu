package fr.quatrevieux.araknemu.data.world.repository.environment;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;

import java.util.Collection;

/**
 * Repository interface for {@link fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger}
 */
public interface MapTriggerRepository extends Repository<MapTrigger> {
    /**
     * Get all triggers from a map
     */
    public Collection<MapTrigger> findByMap(MapTemplate map);

    /**
     * Get all triggers
     */
    public Collection<MapTrigger> all();
}

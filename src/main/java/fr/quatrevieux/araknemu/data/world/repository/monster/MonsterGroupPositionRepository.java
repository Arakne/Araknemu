package fr.quatrevieux.araknemu.data.world.repository.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;

import java.util.Collection;

/**
 * Repository for {@link MonsterGroupPosition}
 */
public interface MonsterGroupPositionRepository extends Repository<MonsterGroupPosition> {
    /**
     * Get all available monster groups for a map
     * If no group is found for the map, an empty list is returned
     *
     * @param mapId The map id
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate#id()
     */
    public Collection<MonsterGroupPosition> byMap(int mapId);

    /**
     * Get all monster groups
     */
    public Collection<MonsterGroupPosition> all();
}

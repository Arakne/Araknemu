package fr.quatrevieux.araknemu.data.world.repository.environment.npc;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;

import java.util.Collection;

/**
 * Repository for load NPC
 *
 * @see Npc
 */
public interface NpcRepository extends Repository<Npc> {
    /**
     * Get a npc by its id
     */
    public Npc get(int id);

    /**
     * Get all NPCs on the given map
     *
     * @param mapId The map ID
     */
    public Collection<Npc> byMapId(int mapId);

    /**
     * Get all npc templates
     */
    public Collection<Npc> all();
}

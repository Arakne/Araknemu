package fr.quatrevieux.araknemu.data.world.repository.environment.npc;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;

import java.util.Collection;

/**
 * Repository for load NPC templates
 *
 * @see fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate
 */
public interface NpcTemplateRepository extends Repository<NpcTemplate> {
    /**
     * Get a npc by its id
     */
    public NpcTemplate get(int id);

    /**
     * Get all npc templates
     */
    public Collection<NpcTemplate> all();
}

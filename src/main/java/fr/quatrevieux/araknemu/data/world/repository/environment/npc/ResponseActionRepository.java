package fr.quatrevieux.araknemu.data.world.repository.environment.npc;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Repository for load NPC response
 *
 * @see ResponseAction
 */
public interface ResponseActionRepository extends Repository<ResponseAction> {
    /**
     * Get list of responses by the question
     *
     * @return List of response actions, grouping by the response id
     */
    public Map<Integer, List<ResponseAction>> byQuestion(Question question);

    /**
     * Get all responses actions, grouping by response id
     */
    public Map<Integer, List<ResponseAction>> all();
}

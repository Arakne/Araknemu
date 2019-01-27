package fr.quatrevieux.araknemu.data.world.repository.environment.npc;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;

import java.util.Collection;

/**
 * Repository for load NPC questions
 *
 * @see Question
 */
public interface QuestionRepository extends Repository<Question> {
    /**
     * Get a question by its id
     */
    public Question get(int id);

    /**
     * Get initial questions for a NPC
     */
    public Collection<Question> byNpc(Npc npc);

    /**
     * Get list of questions by ids
     */
    public Collection<Question> byIds(int[] ids);

    /**
     * Get all questions
     */
    public Collection<Question> all();
}

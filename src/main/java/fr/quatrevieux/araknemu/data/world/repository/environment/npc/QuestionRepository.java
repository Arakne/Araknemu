/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

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

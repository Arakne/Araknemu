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

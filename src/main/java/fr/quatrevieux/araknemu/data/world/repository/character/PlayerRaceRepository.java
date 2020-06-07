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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.world.repository.character;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;

import java.util.Collection;

/**
 * Read only repository for {@link PlayerRace}
 */
public interface PlayerRaceRepository extends Repository<PlayerRace> {
    /**
     * Get a race entity by its Race enum value
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the player race
     * @throws RepositoryException When an error occurs during retrieving data
     */
    PlayerRace get(Race race) throws RepositoryException;

    /**
     * Load all player races
     */
    public Collection<PlayerRace> load();
}

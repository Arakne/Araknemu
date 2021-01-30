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

package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Cache for {@link PlayerRaceRepository}
 */
final public class PlayerRaceRepositoryCache implements PlayerRaceRepository {
    final private PlayerRaceRepository repository;

    final private Map<Race, PlayerRace> races = new EnumMap<>(Race.class);

    public PlayerRaceRepositoryCache(PlayerRaceRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlayerRace get(Race race) throws RepositoryException {
        if (!races.containsKey(race)) {
            races.put(race, repository.get(race));
        }

        return races.get(race);
    }

    @Override
    public void initialize() throws RepositoryException {
        repository.initialize();
    }

    @Override
    public void destroy() throws RepositoryException {
        repository.destroy();
    }

    @Override
    public PlayerRace get(PlayerRace entity) throws RepositoryException {
        return get(entity.race());
    }

    @Override
    public boolean has(PlayerRace entity) throws RepositoryException {
        return races.containsKey(entity.race()) || repository.has(entity);
    }

    @Override
    public Collection<PlayerRace> load() {
        Collection<PlayerRace> loaded = repository.load();

        for (PlayerRace race : loaded) {
            races.put(race.race(), race);
        }

        return loaded;
    }
}

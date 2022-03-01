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

package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link NpcTemplate}
 */
public final class NpcTemplateRepositoryCache implements NpcTemplateRepository {
    private final NpcTemplateRepository repository;

    private final ConcurrentMap<Integer, NpcTemplate> cacheById = new ConcurrentHashMap<>();

    public NpcTemplateRepositoryCache(NpcTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public NpcTemplate get(int id) {
        return cacheById.computeIfAbsent(id, repository::get);
    }

    @Override
    public Collection<NpcTemplate> all() {
        final Collection<NpcTemplate> loaded = repository.all();

        for (NpcTemplate template : loaded) {
            cacheById.put(template.id(), template);
        }

        return loaded;
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
    public NpcTemplate get(NpcTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(NpcTemplate entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}

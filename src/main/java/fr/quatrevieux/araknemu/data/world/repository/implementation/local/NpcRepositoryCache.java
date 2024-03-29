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
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link Npc}
 */
public final class NpcRepositoryCache implements NpcRepository {
    private final NpcRepository repository;

    private final ConcurrentMap<Integer, Npc> cacheById = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Collection<Npc>> cacheByMap = new ConcurrentHashMap<>();

    private boolean loaded = false;

    public NpcRepositoryCache(NpcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Npc get(int id) {
        Npc npc = cacheById.get(id);

        if (npc == null) {
            npc = repository.get(id);
            saveInCache(npc);
        }

        return npc;
    }

    @Override
    public Collection<Npc> all() {
        final Collection<Npc> loaded = repository.all();

        loaded.forEach(this::saveInCache);
        this.loaded = true;

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
    public Npc get(Npc entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(Npc entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }

    @Override
    public Collection<Npc> byMapId(int mapId) {
        Collection<Npc> entities = cacheByMap.get(mapId);

        if (entities != null) {
            return entities;
        }

        if (loaded) {
            return Collections.emptyList();
        }

        entities = repository.byMapId(mapId);
        entities.forEach(this::saveInCache);

        return entities;
    }

    private void saveInCache(Npc entity) {
        cacheById.put(entity.id(), entity);
        cacheByMap
            .computeIfAbsent(entity.position().map(), mapId -> new ArrayList<>())
            .add(entity)
        ;
    }
}

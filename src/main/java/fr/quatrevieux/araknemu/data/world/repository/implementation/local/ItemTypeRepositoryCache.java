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
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link ItemType}
 */
public final class ItemTypeRepositoryCache implements ItemTypeRepository {
    private final ItemTypeRepository repository;

    private final ConcurrentMap<Integer, ItemType> cacheById = new ConcurrentHashMap<>();

    public ItemTypeRepositoryCache(ItemTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public ItemType get(int id) {
        return cacheById.computeIfAbsent(id, repository::get);
    }

    @Override
    public Collection<ItemType> load() {
        final Collection<ItemType> loaded = repository.load();

        for (ItemType template : loaded) {
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
    public ItemType get(ItemType entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(ItemType entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}

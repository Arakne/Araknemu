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
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate}
 */
public final class ItemTemplateRepositoryCache implements ItemTemplateRepository {
    private final ItemTemplateRepository repository;

    private final ConcurrentMap<Integer, ItemTemplate> cacheById = new ConcurrentHashMap<>();

    public ItemTemplateRepositoryCache(ItemTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public ItemTemplate get(int id) {
        return cacheById.computeIfAbsent(id, repository::get);
    }

    @Override
    public Collection<ItemTemplate> load() {
        final Collection<ItemTemplate> loaded = repository.load();

        for (ItemTemplate template : loaded) {
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
    public ItemTemplate get(ItemTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(ItemTemplate entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}

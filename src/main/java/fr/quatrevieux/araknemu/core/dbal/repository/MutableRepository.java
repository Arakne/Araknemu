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

package fr.quatrevieux.araknemu.core.dbal.repository;

/**
 * Repository for savable entity
 * @param <E> The entity type
 */
public interface MutableRepository<E> extends Repository<E> {
    /**
     * Save the entity into the repository
     * On creation with PK generation (auto-increment), the returned entity will contains the generated PK
     *
     * @param entity Entity to save
     *
     * @return The entity instance related to repository.
     *
     * @throws RepositoryException When error occurs during saving
     */
    public E add(E entity) throws RepositoryException;

    /**
     * Delete an entity from the repository
     *
     * @param entity Entity to delete
     *
     * @throws RepositoryException When error occurs during saving
     */
    public void delete(E entity) throws RepositoryException;
}

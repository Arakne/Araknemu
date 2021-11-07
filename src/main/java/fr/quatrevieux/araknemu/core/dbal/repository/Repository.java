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
 * Base repository interface
 * @param <E> The handled entity type
 */
public interface Repository<E> {
    /**
     * Initialize repository
     */
    public void initialize() throws RepositoryException;

    /**
     * Destroy the repository (drop)
     */
    public void destroy() throws RepositoryException;

    /**
     * Get an entity from the repository
     *
     * @param entity The entity to find (used as primary key criteria)
     *
     * @return The repository entity. The returned value MUST NOT be null
     *
     * @throws RepositoryException When an error occurs during retrieving data
     * @throws EntityNotFoundException When the entity cannot be found
     *
     * @see Repository#has(Object) To check if the entity exists
     */
    public E get(E entity) throws RepositoryException;

    /**
     * Check if an entity exists in the repository
     *
     * @param entity The entity to find (used as primary key criteria)
     *
     * @return true if exists
     *
     * @throws RepositoryException When an error occurs during retrieving data
     */
    public boolean has(E entity) throws RepositoryException;
}

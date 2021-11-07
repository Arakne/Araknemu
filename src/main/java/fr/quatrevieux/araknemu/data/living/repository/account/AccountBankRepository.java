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

package fr.quatrevieux.araknemu.data.living.repository.account;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;

/**
 * Repository for account bank
 */
public interface AccountBankRepository extends MutableRepository<AccountBank> {
    /**
     * Get the account bank
     * If the entity is not found, the given entity (parameter) is returned
     * No exceptions is thrown when entity is not found
     *
     * @param entity The entity to find (used as primary key criteria)
     *
     * @return The database entity if found, or the given entity if not
     * @throws RepositoryException When a DBAL error occurs
     */
    @Override
    public AccountBank get(AccountBank entity) throws RepositoryException;
}

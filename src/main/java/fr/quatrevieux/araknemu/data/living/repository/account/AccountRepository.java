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

package fr.quatrevieux.araknemu.data.living.repository.account;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository for accounts
 */
public interface AccountRepository extends MutableRepository<Account> {
    /**
     * Find account by its username (for authenticate)
     */
    public Account findByUsername(String username) throws RepositoryException;

    /**
     * Find an account by its pseudo
     * If the account is not found, an empty optional is returned
     */
    public Optional<Account> findByPseudo(String pseudo);

    /**
     * Update the password field of the account
     *
     * @param account Account to update
     */
    public void savePassword(Account account);

    /**
     * Find multiple accounts by ids
     * If an account id is not found, it will be ignored
     *
     * Note: The result order is undefined
     *
     * @param ids The account ids
     *
     * @return List of accounts
     */
    public Collection<Account> findByIds(int[] ids);
}

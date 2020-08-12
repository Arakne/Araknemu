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
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;

import java.util.List;

/**
 * Repository for {@link Banishment} entity
 */
public interface BanishmentRepository extends MutableRepository<Banishment> {
    /**
     * Check if the given account is banned at the current time
     *
     * @param accountId Account id to check
     *
     * @return true if the account is banned (and should not be logged in)
     */
    public boolean isBanned(int accountId);

    /**
     * Get list of all banishment for the given account
     * The active and past banishment are returned
     * The entries are sorted by start date in reverse order (from recent to old)
     *
     * @param accountId The account to check
     *
     * @return List of banishment
     */
    public List<Banishment> forAccount(int accountId);

    /**
     * Remove all active banishment for the given account
     *
     * @param accountId The account id
     */
    public void removeActive(int accountId);
}

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

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Collection;

/**
 * Repository for {@link BankItem}
 */
public interface BankItemRepository extends MutableRepository<BankItem> {
    /**
     * Get items of a bank account
     *
     * @param bank The bank account to load
     */
    public Collection<BankItem> byBank(AccountBank bank);

    /**
     * Update the item
     * Save quantity and position
     *
     * @param item Item to save
     *
     * @throws EntityNotFoundException When no items is updated
     */
    public void update(BankItem item);

    /**
     * Delete the item from database
     *
     * @param item Item to delete
     *
     * @throws EntityNotFoundException When cannot found entity to delete
     */
    public void delete(BankItem item);

    /**
     * Count the number of stored item entries into the bank
     *
     * @param bank The bank account
     *
     * @return The number of item entries
     */
    public @NonNegative int count(AccountBank bank);
}

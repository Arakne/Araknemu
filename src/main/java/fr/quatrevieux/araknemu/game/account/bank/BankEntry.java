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

package fr.quatrevieux.araknemu.game.account.bank;

import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.AbstractItemEntry;
import org.checkerframework.common.value.qual.IntVal;

/**
 * Entry for a bank item
 * Because a bank doesn't have item slots, the entries are always on default position (-1)
 */
public final class BankEntry extends AbstractItemEntry {
    private final BankItem entity;

    public BankEntry(Bank bank, BankItem entity, Item item) {
        super(bank, entity, item, bank);

        this.entity = entity;
    }

    @Override
    public @IntVal(DEFAULT_POSITION) int position() {
        return DEFAULT_POSITION;
    }

    /**
     * Get the database entity
     */
    public BankItem entity() {
        return entity;
    }
}

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

package fr.quatrevieux.araknemu.game.exploration.exchange.event;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeStorage;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * An item has been moved on the exchange
 */
final public class ItemMoved implements ExchangeEvent {
    final private ItemEntry entry;
    final private int quantity;
    final private ExchangeStorage storage;

    public ItemMoved(ItemEntry entry, int quantity, ExchangeStorage storage) {
        this.entry = entry;
        this.quantity = quantity;
        this.storage = storage;
    }

    /**
     * The moved item
     */
    public ItemEntry entry() {
        return entry;
    }

    /**
     * The new quantity
     */
    public int quantity() {
        return quantity;
    }

    @Override
    public ExchangeStorage storage() {
        return storage;
    }
}

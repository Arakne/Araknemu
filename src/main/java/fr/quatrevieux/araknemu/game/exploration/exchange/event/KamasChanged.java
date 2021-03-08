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

/**
 * The quantity of kamas in the exchange has changed
 */
public final class KamasChanged implements ExchangeEvent {
    private final long quantity;
    private final ExchangeStorage storage;

    public KamasChanged(long quantity, ExchangeStorage storage) {
        this.quantity = quantity;
        this.storage = storage;
    }

    /**
     * The new kamas quantity
     */
    public long quantity() {
        return quantity;
    }

    @Override
    public ExchangeStorage storage() {
        return storage;
    }
}

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

package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.WalletEntity;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Base implementation of Wallet
 */
public final class SimpleWallet implements Wallet {
    private final WalletEntity entity;
    private final Dispatcher dispatcher;

    public SimpleWallet(WalletEntity entity, Dispatcher dispatcher) {
        this.entity = entity;
        this.dispatcher = dispatcher;
    }

    @Override
    public @NonNegative long kamas() {
        return entity.kamas();
    }

    @Override
    public void addKamas(@Positive long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }

        updateKamas(quantity);
    }

    @Override
    public void removeKamas(@Positive long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }

        if (quantity > entity.kamas()) {
            throw new IllegalArgumentException("Quantity is too high");
        }

        updateKamas(-quantity);
    }

    /**
     * Update quantity of kamas, and trigger {@link KamasChanged} event
     *
     * @param quantity The change quantity. Positive for adding, negative for remove
     */
    private void updateKamas(long quantity) {
        final long last = entity.kamas();

        entity.setKamas(Math.max(last + quantity, 0));
        dispatcher.dispatch(new KamasChanged(last, entity.kamas()));
    }
}

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

package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeProcessor;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * The base player party for an exchange between two parties
 * This implementation is thread safe : all modifier methods are synchronized on both parties
 */
public abstract class AbstractPlayerExchangeParty implements ExchangeParty {
    private final ExplorationPlayer player;
    private final PlayerExchangeStorage storage;

    /**
     * The common exchange processor
     * The instance is shared between two parties, and is used for synchronization
     * All modifier methods must be synchronized with this instance
     */
    private final ExchangeProcessor processor;

    protected AbstractPlayerExchangeParty(ExplorationPlayer player, ExchangeProcessor processor, PlayerExchangeStorage storage) {
        this.player = player;
        this.processor = processor;
        this.storage = storage;
    }

    @Override
    public final ExplorationPlayer actor() {
        return player;
    }

    @Override
    public final ExchangeInteraction dialog() {
        return new ExchangeDialog(this);
    }

    @Override
    public final void leave() {
        processor.cancel();
    }

    @Override
    public final void toggleAccept() {
        synchronized (processor) {
            processor.assertNotAccepted();

            storage.setAccepted(!storage.accepted());

            if (processor.accepted()) {
                processor.process();
            }
        }
    }

    @Override
    public final void kamas(long quantity) {
        synchronized (processor) {
            processor.assertNotAccepted();

            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }

            processor.resetAccept();

            storage.setKamas(Math.min(quantity, player.inventory().kamas()));
        }
    }

    @Override
    public final void item(int itemEntryId, int quantity) {
        final ItemEntry entry = player.inventory().get(itemEntryId);

        synchronized (processor) {
            processor.assertNotAccepted();
            processor.resetAccept();

            final int newQuantity = storage.quantity(entry) + quantity;

            if (newQuantity <= 0) {
                storage.setItem(entry, 0);
            } else {
                storage.setItem(entry, Math.min(newQuantity, entry.quantity()));
            }
        }
    }

    @Override
    public final void send(Object packet) {
        player.send(packet);
    }
}

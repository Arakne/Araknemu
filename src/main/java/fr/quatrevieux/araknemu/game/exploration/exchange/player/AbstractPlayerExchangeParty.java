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
abstract public class AbstractPlayerExchangeParty implements ExchangeParty {
    final private ExplorationPlayer player;
    final private PlayerExchangeStorage storage;

    /**
     * The common exchange processor
     * The instance is shared between two parties, and is used for synchronization
     * All modifier methods must be synchronized with this instance
     */
    final private ExchangeProcessor processor;


    protected AbstractPlayerExchangeParty(ExplorationPlayer player, ExchangeProcessor processor, PlayerExchangeStorage storage) {
        this.player = player;
        this.processor = processor;
        this.storage = storage;
    }

    @Override
    final public ExplorationPlayer actor() {
        return player;
    }

    @Override
    final public ExchangeInteraction dialog() {
        return new ExchangeDialog(this);
    }

    @Override
    final public void leave() {
        processor.cancel();
    }

    @Override
    final public void toggleAccept() {
        synchronized (processor) {
            processor.assertNotAccepted();

            storage.setAccepted(!storage.accepted());

            if (processor.accepted()) {
                processor.process();
            }
        }
    }

    @Override
    final public void kamas(long quantity) {
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
    final public void item(int itemEntryId, int quantity) {
        final ItemEntry entry = player.inventory().get(itemEntryId);

        synchronized (processor) {
            processor.assertNotAccepted();
            processor.resetAccept();

            storage.setItem(entry, Math.min(storage.quantity(entry) + quantity, entry.quantity()));
        }
    }

    @Override
    final public void send(Object packet) {
        player.send(packet);
    }
}

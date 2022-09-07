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

package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcStoreExchange;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemBought;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemSold;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Dialog box interaction for a store
 *
 * @todo refactor with player store
 */
public final class StoreDialog implements ExchangeInteraction {
    private final NpcStoreExchange exchange;

    public StoreDialog(NpcStoreExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void leave() {
        exchange.stop();
        exchange.send(ExchangeLeaved.accepted());
    }

    @Override
    public Interaction start() {
        exchange.send(new ExchangeCreated(ExchangeType.NPC_STORE, exchange.seller()));
        exchange.initialize();

        return this;
    }

    @Override
    public void stop() {
        leave();
    }

    /**
     * Buy the item
     *
     * @param itemId The item id (item template id for the npc store)
     * @param quantity The asked quantity
     */
    public void buy(int itemId, @Positive int quantity) {
        try {
            exchange.buy(itemId, quantity);
            exchange.send(ItemBought.success());
        } catch (IllegalArgumentException e) {
            exchange.send(ItemBought.failed());
        }
    }

    /**
     * Sell the item
     *
     * @param itemId The inventory item entry id
     * @param quantity The sell quantity
     */
    public void sell(int itemId, @Positive int quantity) {
        try {
            exchange.sell(itemId, quantity);
            exchange.send(ItemSold.success());
        } catch (InventoryException e) {
            exchange.send(ItemSold.failed());
        }
    }
}

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

package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.Exchange;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStore;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.NpcStoreList;

/**
 * Exchange logic for the NPC store
 *
 * @todo refactor with player store (use interface)
 */
public final class NpcStoreExchange implements Exchange, Sender {
    private final ExplorationPlayer player;
    private final GameNpc seller;
    private final NpcStore store;

    public NpcStoreExchange(ExplorationPlayer player, GameNpc seller, NpcStore store) {
        this.player = player;
        this.seller = seller;
        this.store = store;
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    @Override
    public void initialize() {
        player.send(new NpcStoreList(store.available()));
    }

    @Override
    public StoreDialog dialog() {
        return new StoreDialog(this);
    }

    /**
     * The the seller npc
     */
    public GameNpc seller() {
        return seller;
    }

    /**
     * Stop interaction
     */
    public void stop() {
        player.interactions().remove();
    }

    /**
     * Buy an item
     *
     * @param itemId The item template id
     * @param quantity The buy quantity
     *
     * @throws IllegalArgumentException When invalid item, quantity is given, or player doesn't have enough kamas
     */
    public void buy(int itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        if (!store.has(itemId)) {
            throw new IllegalArgumentException("Item not available");
        }

        final NpcStore.Sell sell = store.buy(itemId, quantity);

        player.inventory().removeKamas(sell.price());
        sell.items().forEach((item, itemQuantity) -> player.inventory().add(item, itemQuantity));
    }

    /**
     * Sell an item to the NPC
     *
     * @param itemId The inventory entry id
     * @param quantity Quantity to sell
     *
     * @throws fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException When cannot sell the item
     */
    public void sell(int itemId, int quantity) {
        final ItemEntry entry = player.inventory().get(itemId);
        final long price = store.sellPrice(entry.item(), quantity);

        entry.remove(quantity);

        if (price > 0) {
            player.inventory().addKamas(price);
        }
    }
}

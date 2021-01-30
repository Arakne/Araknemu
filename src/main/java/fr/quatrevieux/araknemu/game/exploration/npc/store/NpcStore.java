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

package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcStoreExchange;
import fr.quatrevieux.araknemu.game.exploration.npc.ExchangeProvider;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Npc store
 *
 * Note: the store is related to a npc template and not a npc, so the same store can be shared between two npcs
 */
final public class NpcStore implements ExchangeProvider.Factory {
    final private ItemService itemService;
    final private GameConfiguration.EconomyConfiguration configuration;
    final private Map<Integer, ItemTemplate> itemTemplates;

    public NpcStore(ItemService itemService, GameConfiguration.EconomyConfiguration configuration, Collection<ItemTemplate> itemTemplates) {
        this.itemService = itemService;
        this.configuration = configuration;
        this.itemTemplates = itemTemplates.stream().collect(Collectors.toMap(ItemTemplate::id, Function.identity()));
    }

    @Override
    public NpcStoreExchange create(ExplorationPlayer initiator, GameNpc npc) {
        return new NpcStoreExchange(initiator, npc, this);
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.NPC_STORE;
    }

    /**
     * Get all available item templates
     */
    public Collection<ItemTemplate> available() {
        return itemTemplates.values();
    }

    /**
     * Check if the given item template is available
     *
     * @param id The item template id
     *
     * @return true if available
     */
    public boolean has(int id) {
        return itemTemplates.containsKey(id);
    }

    /**
     * Try to generate the items
     *
     * Note: Each items are regenerated, so items may have different stats and quantity
     *
     * @param id The item template id
     * @param quantity The asked quantity
     *
     * @return Map of generated item, associated with quantity
     */
    public Map<Item, Integer> get(int id, int quantity) {
        return itemService.createBulk(itemTemplates.get(id), quantity);
    }

    /**
     * Get the price for generate given items
     *
     * @param id The item template id
     * @param quantity The asked quantity
     *
     * @return The price
     */
    public long price(int id, int quantity) {
        return itemTemplates.get(id).price() * quantity;
    }

    /**
     * Get the price for sell the item to the NPC
     *
     * @param item Item to sell
     * @param quantity Quantity to sell
     *
     * @return The cost in kamas
     */
    public long sellPrice(Item item, int quantity) {
        final long basePrice = (long) (configuration.npcSellPriceMultiplier() * item.template().price());

        return basePrice * quantity;
    }
}

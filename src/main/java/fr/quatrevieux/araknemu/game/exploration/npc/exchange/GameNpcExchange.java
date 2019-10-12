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

package fr.quatrevieux.araknemu.game.exploration.npc.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.npc.ExchangeProvider;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exchange data for an Npc
 */
final public class GameNpcExchange implements ExchangeProvider.Factory {
    final private List<NpcExchangeEntry> entries;

    public GameNpcExchange(List<NpcExchangeEntry> entries) {
        this.entries = entries;
    }

    @Override
    public NpcExchangeParty create(ExplorationPlayer initiator, GameNpc npc) {
        return new NpcExchangeParty(initiator, npc, this);
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.NPC_EXCHANGE;
    }

    /**
     * Find a matching exchange entry
     *
     * @param exchangedItems Items (with there quantity) exchanged by the player
     * @param exchangedKamas Kamas exchanged by the player
     *
     * @return The first matching entry, or null entry if not found
     */
    public NpcExchangeEntry find(Map<ItemEntry, Integer> exchangedItems, long exchangedKamas) {
        final Map<Integer, Integer> exchangedItemTemplatesWithQuantity = new HashMap<>();

        exchangedItems.forEach((itemEntry, quantity) -> exchangedItemTemplatesWithQuantity.put(
            itemEntry.templateId(),
            exchangedItemTemplatesWithQuantity.getOrDefault(itemEntry.templateId(), 0) + quantity
        ));

        return entries.stream()
            .filter(entry -> entry.match(exchangedItemTemplatesWithQuantity, exchangedKamas))
            .findFirst()
            .orElse(NpcExchangeEntry.NULL_ENTRY)
        ;
    }
}

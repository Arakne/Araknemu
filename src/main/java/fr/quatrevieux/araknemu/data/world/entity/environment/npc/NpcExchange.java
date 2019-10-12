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

package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

import java.util.Map;

/**
 * Npc exchange data
 *
 * Note: The exchange is related to a npc template, not a npc
 */
final public class NpcExchange {
    final private int id;
    final private int npcTemplateId;
    final private long requiredKamas;
    final private Map<Integer, Integer> requiredItems;
    final private long exchangedKamas;
    final private Map<Integer, Integer> exchangedItems;

    public NpcExchange(int id, int npcTemplateId, long requiredKamas, Map<Integer, Integer> requiredItems, long exchangedKamas, Map<Integer, Integer> exchangedItems) {
        this.id = id;
        this.npcTemplateId = npcTemplateId;
        this.requiredKamas = requiredKamas;
        this.requiredItems = requiredItems;
        this.exchangedKamas = exchangedKamas;
        this.exchangedItems = exchangedItems;
    }

    /**
     * The id of the exchange (primary key)
     */
    public int id() {
        return id;
    }

    /**
     * The related npc template
     *
     * @see NpcTemplate#id()
     */
    public int npcTemplateId() {
        return npcTemplateId;
    }

    /**
     * The required amount of kamas for perform the exchange
     */
    public long requiredKamas() {
        return requiredKamas;
    }

    /**
     * Map of required items templates for perform the exchange,
     * with item template id as key, and quantity as value
     *
     * Format: [itemTemplateId]:[quantity];[item2]:[quantity2]
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()
     */
    public Map<Integer, Integer> requiredItems() {
        return requiredItems;
    }

    /**
     * The amount of exchanged kamas
     */
    public long exchangedKamas() {
        return exchangedKamas;
    }

    /**
     * List of exchanged items templates,
     * with item template id as key, and quantity as value
     *
     * Format: [itemTemplateId]:[quantity];[item2]:[quantity2]
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()
     */
    public Map<Integer, Integer> exchangedItems() {
        return exchangedItems;
    }
}

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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collection;

/**
 * DTO for store loaded items of a player before its initialisation
 */
public final class LoadedInventory {
    private final Player player;
    private final Collection<InventoryService.LoadedItem> items;

    public LoadedInventory(Player player, Collection<InventoryService.LoadedItem> items) {
        this.player = player;
        this.items = items;
    }

    /**
     * Attach the inventory to its owner
     */
    public PlayerInventory attach(GamePlayer owner) {
        return new PlayerInventory(owner, player, items);
    }
}

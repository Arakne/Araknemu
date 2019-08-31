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

package fr.quatrevieux.araknemu.data.living.repository.player;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Repository for {@link PlayerItem}
 */
public interface PlayerItemRepository extends MutableRepository<PlayerItem> {
    /**
     * Get items by player
     *
     * @param player The player to load
     */
    public Collection<PlayerItem> byPlayer(Player player);

    /**
     * Get list of equiped items for all players from an account
     *
     * @param serverId The server to load
     * @param accountId The account
     * @param positions The equiped items positions
     *
     * @return List of items by player id
     */
    public Map<Integer, List<PlayerItem>> forCharacterList(int serverId, int accountId, int[] positions);

    /**
     * Update the item
     * Save quantity and position
     *
     * @param item Item to save
     *
     * @throws EntityNotFoundException When no items is updated
     */
    public void update(PlayerItem item);

    /**
     * Delete the item from database
     *
     * @param item Item to delete
     *
     * @throws EntityNotFoundException When cannot found entity to delete
     */
    public void delete(PlayerItem item);
}

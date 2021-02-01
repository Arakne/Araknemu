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

package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.type.AbstractEquipment;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All available item sets for a player
 */
public final class ItemSets {
    private final PlayerInventory inventory;

    public ItemSets(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Get player item set
     * If the item set is not found, and empty item set will be returned
     *
     * @param itemSet Item set to get
     */
    public PlayerItemSet get(GameItemSet itemSet) {
        return new PlayerItemSet(
            itemSet,
            inventory.equipments()
                .stream()
                .filter(equipment -> equipment.set().filter(set -> set.id() == itemSet.id()).isPresent())
                .map(Item::template)
                .collect(Collectors.toSet())
        );
    }

    /**
     * Get all player item sets
     */
    public Collection<PlayerItemSet> all() {
        final Map<Integer, PlayerItemSet> sets = new HashMap<>();

        for (AbstractEquipment equipment : inventory.equipments()) {
            equipment.set().ifPresent(set -> {
                if (!sets.containsKey(set.id())) {
                    sets.put(set.id(), new PlayerItemSet(set));
                }

                sets.get(set.id()).add(equipment.template());
            });
        }

        return sets.values();
    }

    /**
     * Apply item set effects to player characteristics
     */
    public void apply(MutableCharacteristics characteristics) {
        for (PlayerItemSet itemSet : all()) {
            itemSet.apply(characteristics);
        }
    }

    /**
     * Apply special effects
     */
    public void applySpecials(GamePlayer player) {
        for (PlayerItemSet itemSet : all()) {
            itemSet.applySpecials(player);
        }
    }
}

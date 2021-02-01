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

package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.inventory.AbstractItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

import java.util.stream.Collectors;

/**
 * Entry for player repository
 */
final public class InventoryEntry extends AbstractItemEntry {
    final private PlayerInventory inventory;
    final private PlayerItem entity;
    final private Item item;

    public InventoryEntry(PlayerInventory inventory, PlayerItem entity, Item item) {
        super(inventory, entity, item, inventory);

        this.inventory = inventory;
        this.entity = entity;
        this.item = item;
    }

    @Override
    public int position() {
        return entity.position();
    }

    /**
     * Move the entry to a new position
     *
     * @param position The new position
     * @param quantity Quantity to move
     */
    public void move(int position, int quantity) throws InventoryException {
        if (quantity > quantity() || quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        if (quantity == quantity()) {
            if (inventory.move(this, position)) {
                changePosition(position);
            }

            return;
        }

        inventory.add(item, quantity, position);
        remove(quantity);
    }

    /**
     * Set the item to the default position
     * Note: this method is internal and should not be called
     */
    public void setToDefaultPosition() {
        entity.setPosition(DEFAULT_POSITION);
    }

    /**
     * Get the database entity
     */
    PlayerItem entity() {
        return entity;
    }

    private void changePosition(int position) {
        entity.setPosition(position);
        inventory.dispatch(new ObjectMoved(this));
    }

    /**
     * Create a new entry
     */
    static InventoryEntry create(PlayerInventory inventory, int id, Item item, int quantity, int position) {
        return new InventoryEntry(
            inventory,
            new PlayerItem(
                inventory.owner().id(),
                id,
                item.template().id(),
                item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()),
                quantity,
                position
            ),
            item
        );
    }
}

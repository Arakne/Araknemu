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

package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

import java.util.List;

/**
 * Base implementation for inventory item entry
 */
public abstract class AbstractItemEntry implements ItemEntry {
    private final ItemStorage storage;
    private final fr.quatrevieux.araknemu.data.living.entity.Item entity;
    private final Item item;
    private final Dispatcher dispatcher;

    public AbstractItemEntry(ItemStorage<? extends AbstractItemEntry> storage, fr.quatrevieux.araknemu.data.living.entity.Item entity, Item item, Dispatcher dispatcher) {
        this.storage = storage;
        this.entity = entity;
        this.item = item;
        this.dispatcher = dispatcher;
    }

    @Override
    public final int id() {
        return entity.entryId();
    }

    @Override
    public final Item item() {
        return item;
    }

    @Override
    public final int quantity() {
        return entity.quantity();
    }

    @Override
    public final void add(int quantity) {
        if (quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        changeQuantity(quantity() + quantity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void remove(int quantity) throws InventoryException {
        if (quantity > quantity() || quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        if (quantity == quantity()) {
            entity.setQuantity(0);
            storage.delete(this);
            return;
        }

        changeQuantity(quantity() - quantity);
    }

    @Override
    public final List<ItemTemplateEffectEntry> effects() {
        return entity.effects();
    }

    @Override
    public final int templateId() {
        return entity.itemTemplateId();
    }

    private void changeQuantity(int quantity) {
        entity.setQuantity(quantity);
        dispatcher.dispatch(new ObjectQuantityChanged(this));
    }
}

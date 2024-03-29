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

package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.AbstractEquipment;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;

import java.util.Optional;

/**
 * Base slot class for equipments
 */
public abstract class AbstractEquipmentSlot implements InventorySlot {
    private final Dispatcher dispatcher;
    private final InventorySlot slot;

    public AbstractEquipmentSlot(Dispatcher dispatcher, InventorySlot slot) {
        this.dispatcher = dispatcher;
        this.slot = slot;
    }

    @Override
    public @IntRange(from = -1, to = 57) int id() {
        return slot.id();
    }

    @Override
    public Optional<InventoryEntry> entry() {
        return slot.entry();
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        final InventoryEntry newEntry = slot.set(entry);

        dispatcher.dispatch(new EquipmentChanged(entry, id(), true));

        return newEntry;
    }

    @Override
    public InventoryEntry set(Item item, @Positive int quantity) throws InventoryException {
        final InventoryEntry entry = slot.set(item, quantity);

        dispatcher.dispatch(new EquipmentChanged(entry, id(), true));

        return entry;
    }

    @Override
    public void unset() {
        entry().ifPresent(entry -> {
            slot.unset();
            dispatcher.dispatch(new EquipmentChanged(entry, id(), false));
        });
    }

    @Override
    public void uncheckedSet(@Nullable InventoryEntry entry) {
        slot.uncheckedSet(entry);
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        slot.check(item, quantity);
    }

    /**
     * Get the current equipment
     */
    public Optional<AbstractEquipment> equipment() {
        return entry().map(InventoryEntry::item).map(AbstractEquipment.class::cast);
    }
}

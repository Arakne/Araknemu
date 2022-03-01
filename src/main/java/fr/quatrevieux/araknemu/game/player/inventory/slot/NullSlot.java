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

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * Null object for slot
 */
public final class NullSlot implements InventorySlot {
    private final int id;

    public NullSlot(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Optional<InventoryEntry> entry() {
        return Optional.empty();
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        throw new InventoryException("Null slot");
    }

    @Override
    public void uncheckedSet(@Nullable InventoryEntry entry) {
        // Cannot store anything an null slot
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        throw new InventoryException("Null slot");
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        throw new InventoryException("Null slot");
    }
}

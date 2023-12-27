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
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.AbstractEquipment;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.checkerframework.common.value.qual.IntRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * All inventory slots
 */
public final class InventorySlots {
    public static final int SLOT_MAX = 57;

    private final Dispatcher dispatcher;
    private final ItemStorage<InventoryEntry> storage;

    private final InventorySlot defaultSlot;
    private final InventorySlot[] slots = new InventorySlot[SLOT_MAX + 1];

    public InventorySlots(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner) {
        this.dispatcher  = dispatcher;
        this.storage     = storage;
        this.defaultSlot = new DefaultSlot(storage);

        init(owner);
    }

    /**
     * Get an inventory slot
     */
    public InventorySlot get(@IntRange(from = -1, to = InventorySlots.SLOT_MAX) int id) throws InventoryException {
        if (id == ItemEntry.DEFAULT_POSITION) {
            return defaultSlot;
        }

        if (id >= slots.length) {
            throw new InventoryException("Invalid slot");
        }

        return slots[id];
    }

    /**
     * Get current equipments
     */
    public Collection<AbstractEquipment> equipments() {
        final Collection<AbstractEquipment> equipments = new ArrayList<>();

        for (InventorySlot slot : slots) {
            slot.equipment().ifPresent(equipments::add);
        }

        return equipments;
    }

    @SuppressWarnings("array.access.unsafe.low") // Adding default slot (i.e. id = -1) is forbidden
    private void add(InventorySlot slot) {
        slots[slot.id()] = slot;
    }

    /**
     * Init all slots of the owner
     *
     * @param owner The inventory owner
     */
    private void init(GamePlayer owner) {
        initSlots(owner);
        fillSlots();
    }

    private void initSlots(GamePlayer owner) {
        add(new AmuletSlot(dispatcher, storage, owner));
        add(new WeaponSlot(dispatcher, storage, owner));
        add(new RingSlot(dispatcher, storage, owner, this, RingSlot.RING1));
        add(new RingSlot(dispatcher, storage, owner, this, RingSlot.RING2));
        add(new BeltSlot(dispatcher, storage, owner));
        add(new BootsSlot(dispatcher, storage, owner));
        add(new HelmetSlot(dispatcher, storage, owner));
        add(new MantleSlot(dispatcher, storage, owner));
        add(new NullSlot(8)); // pet

        initDofusSlots(owner);
        initNullSlots();
        initUsableSlots();
    }

    private void initDofusSlots(GamePlayer owner) {
        for (int id : DofusSlot.SLOT_IDS) {
            add(new DofusSlot(dispatcher, storage, owner, this, id));
        }
    }

    private void initNullSlots() {
        for (@IntRange(from = 15, to = UsableSlot.SLOT_ID_START) int i = 15; i < UsableSlot.SLOT_ID_START; ++i) {
            add(new NullSlot(i)); // Add null slot for all unhandled slots
        }
    }

    private void initUsableSlots() {
        for (@IntRange(from = UsableSlot.SLOT_ID_START, to = UsableSlot.SLOT_ID_END + 1) int i = UsableSlot.SLOT_ID_START; i <= UsableSlot.SLOT_ID_END; ++i) {
            add(new UsableSlot(storage, i));
        }
    }

    private void fillSlots() {
        for (InventoryEntry entry : storage) {
            try {
                get(entry.position()).uncheckedSet(entry);
            } catch (InventoryException e) {
                entry.setToDefaultPosition();
            }
        }
    }
}

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
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.EquipmentLevelConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemClassConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemTypeConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SingleItemConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Base slot class for wearable
 */
public abstract class AbstractWearableSlot extends AbstractEquipmentSlot {
    public AbstractWearableSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner, @IntRange(from = 0, to = InventorySlots.SLOT_MAX) int id, SuperType type, SlotConstraint[] customConstraints) {
        super(
            dispatcher,
            new SimpleSlot(
                id,
                makeConstraints(owner, type, customConstraints),
                storage
            )
        );
    }

    public AbstractWearableSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner, @IntRange(from = 0, to = InventorySlots.SLOT_MAX) int id, SuperType type) {
        this(dispatcher, storage, owner, id, type, new SlotConstraint[0]);
    }

    private static SlotConstraint[] makeConstraints(GamePlayer owner, SuperType type, SlotConstraint[] customConstraints) {
        final SlotConstraint[] defaultConstraints = new SlotConstraint[] {
            new SingleItemConstraint(),
            new ItemClassConstraint(Wearable.class),
            new ItemTypeConstraint(type),
            new EquipmentLevelConstraint(owner),
        };

        if (customConstraints.length == 0) {
            return defaultConstraints;
        }

        final SlotConstraint[] constraints = new SlotConstraint[defaultConstraints.length + customConstraints.length];

        System.arraycopy(defaultConstraints, 0, constraints, 0, defaultConstraints.length);
        System.arraycopy(customConstraints, 0, constraints, defaultConstraints.length, customConstraints.length);

        return constraints;
    }
}

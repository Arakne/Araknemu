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

package fr.quatrevieux.araknemu.data.living.entity.player;

import fr.quatrevieux.araknemu.data.living.entity.Item;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.common.value.qual.IntRange;

import java.util.List;

/**
 * Inventory entry entity
 */
public final class PlayerItem implements Item {
    private final int playerId;
    private final int entryId;
    private final int itemTemplateId;
    private final List<ItemTemplateEffectEntry> effects;
    private @NonNegative int quantity;
    private @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position;

    public PlayerItem(int playerId, int entryId, int itemTemplateId, List<ItemTemplateEffectEntry> effects, @NonNegative int quantity, @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position) {
        this.playerId = playerId;
        this.entryId = entryId;
        this.itemTemplateId = itemTemplateId;
        this.effects = effects;
        this.quantity = quantity;
        this.position = position;
    }

    public int playerId() {
        return playerId;
    }

    @Override
    public int entryId() {
        return entryId;
    }

    @Override
    public int itemTemplateId() {
        return itemTemplateId;
    }

    @Override
    public List<ItemTemplateEffectEntry> effects() {
        return effects;
    }

    @Override
    public @NonNegative int quantity() {
        return quantity;
    }

    public @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position() {
        return position;
    }

    @Override
    public void setQuantity(@NonNegative int quantity) {
        this.quantity = quantity;
    }

    public void setPosition(@IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position) {
        this.position = position;
    }
}

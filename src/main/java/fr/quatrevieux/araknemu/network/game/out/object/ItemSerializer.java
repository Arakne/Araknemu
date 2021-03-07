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

package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Serialize item entry
 */
public final class ItemSerializer {
    private static final ItemEffectsTransformer EFFECTS_TRANSFORMER = new ItemEffectsTransformer();

    private final ItemEntry entry;

    public ItemSerializer(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return Integer.toHexString(entry.id()) + "~"
            + Integer.toHexString(entry.templateId()) + "~"
            + Integer.toHexString(entry.quantity()) + "~"
            + (entry.isDefaultPosition() ? "" : Integer.toHexString(entry.position())) + "~"
            + EFFECTS_TRANSFORMER.serialize(entry.effects());
    }
}

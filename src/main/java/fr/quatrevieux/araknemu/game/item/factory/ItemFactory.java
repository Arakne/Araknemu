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

package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Factory for items
 */
public interface ItemFactory {
    /**
     * Create the item
     *
     * @param template The item template
     * @param maximize Maximize stats ?
     */
    public Item create(ItemTemplate template, ItemType type, @Nullable GameItemSet set, boolean maximize);

    /**
     * Retrieve an item
     *
     * @param template The item template
     * @param effects The item effects
     */
    public Item retrieve(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<ItemTemplateEffectEntry> effects);

    /**
     * Get the supported item super type
     */
    public SuperType type();
}

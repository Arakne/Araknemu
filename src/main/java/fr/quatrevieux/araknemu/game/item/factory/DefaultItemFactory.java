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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Default item factory implementation
 */
public final class DefaultItemFactory implements ItemFactory {
    private final Map<SuperType, ItemFactory> factories = new EnumMap<>(SuperType.class);

    public DefaultItemFactory(ItemFactory... factories) {
        for (ItemFactory factory : factories) {
            this.factories.put(factory.type(), factory);
        }
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize) {
        if (!factories.containsKey(type.superType())) {
            throw new NoSuchElementException("Invalid type " + type);
        }

        return factories.get(type.superType()).create(template, type, set, maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        if (!factories.containsKey(type.superType())) {
            throw new NoSuchElementException("Invalid type " + type);
        }

        return factories.get(type.superType()).retrieve(template, type, set, effects);
    }

    @Override
    public SuperType type() {
        return null;
    }
}

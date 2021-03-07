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
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.Resource;

import java.util.List;

/**
 * Factory for resources.
 * Will be used as default factory
 */
public final class ResourceFactory implements ItemFactory {
    private final EffectMappers mappers;

    public ResourceFactory(EffectMappers mappers) {
        this.mappers = mappers;
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize) {
        return create(template, type, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, type, effects, false);
    }

    @Override
    public SuperType type() {
        return SuperType.RESOURCE;
    }

    private Item create(ItemTemplate template, ItemType type, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Resource(
            template,
            type,
            mappers.get(SpecialEffect.class).create(effects, maximize)
        );
    }
}

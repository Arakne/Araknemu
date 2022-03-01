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
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMapper;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Factory for usable items.
 */
public final class UsableFactory implements ItemFactory {
    private final EffectMapper<UseEffect> useEffectEffectMapper;
    private final EffectMapper<SpecialEffect> specialEffectEffectMapper;

    public UsableFactory(EffectMapper<UseEffect> useEffectEffectMapper, EffectMapper<SpecialEffect> specialEffectEffectMapper) {
        this.useEffectEffectMapper = useEffectEffectMapper;
        this.specialEffectEffectMapper = specialEffectEffectMapper;
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, @Nullable GameItemSet set, boolean maximize) {
        return create(template, type, template.effects());
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, type, effects);
    }

    @Override
    public SuperType type() {
        return SuperType.USABLE;
    }

    private Item create(ItemTemplate template, ItemType type, List<ItemTemplateEffectEntry> effects) {
        return new UsableItem(
            template,
            type,
            useEffectEffectMapper.create(effects),
            specialEffectEffectMapper.create(effects, true)
        );
    }
}

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
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMapper;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Factory for wearable
 */
public final class WearableFactory implements ItemFactory {
    private final SuperType type;
    private final EffectMapper<CharacteristicEffect> characteristicEffectEffectMapper;
    private final EffectMapper<SpecialEffect> specialEffectEffectMapper;

    public WearableFactory(SuperType type, EffectMapper<CharacteristicEffect> characteristicEffectEffectMapper, EffectMapper<SpecialEffect> specialEffectEffectMapper) {
        this.type = type;
        this.characteristicEffectEffectMapper = characteristicEffectEffectMapper;
        this.specialEffectEffectMapper = specialEffectEffectMapper;
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, @Nullable GameItemSet set, boolean maximize) {
        return create(template, type, set, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, type, set, effects, false);
    }

    @Override
    public SuperType type() {
        return type;
    }

    private Item create(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Wearable(
            template,
            type,
            set,
            characteristicEffectEffectMapper.create(effects, maximize),
            specialEffectEffectMapper.create(effects, maximize)
        );
    }
}

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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.factory;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMapper;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Factory for weapons
 */
public final class WeaponFactory implements ItemFactory {
    private final SpellEffectService effectService;
    private final EffectMapper<WeaponEffect> weaponEffectEffectMapper;
    private final EffectMapper<CharacteristicEffect> characteristicEffectEffectMapper;
    private final EffectMapper<SpecialEffect> specialEffectEffectMapper;

    public WeaponFactory(
        SpellEffectService effectService,
        EffectMapper<WeaponEffect> weaponEffectEffectMapper,
        EffectMapper<CharacteristicEffect> characteristicEffectEffectMapper,
        EffectMapper<SpecialEffect> specialEffectEffectMapper
    ) {
        this.effectService = effectService;
        this.weaponEffectEffectMapper = weaponEffectEffectMapper;
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
        return SuperType.WEAPON;
    }

    private Item create(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        final EffectArea area = type.effectArea();

        if (area == null) {
            throw new IllegalArgumentException("Invalid item type provided");
        }

        return new Weapon(
            template,
            type,
            set,
            weaponEffectEffectMapper.create(effects),
            characteristicEffectEffectMapper.create(effects, maximize),
            specialEffectEffectMapper.create(effects, maximize),
            parseInfo(template.weaponInfo()),
            effectService.area(area)
        );
    }

    private Weapon.WeaponInfo parseInfo(@Nullable String info) {
        if (info == null) {
            throw new IllegalArgumentException("weapon info is missing");
        }

        final String[] parts = StringUtils.split(info, ";");

        return new Weapon.WeaponInfo(
            Integer.parseInt(parts[0]),
            new Interval(
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
            ),
            Integer.parseInt(parts[3]),
            Integer.parseInt(parts[4]),
            Integer.parseInt(parts[5]),
            parts[6].equals("1")
        );
    }
}

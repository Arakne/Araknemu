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

package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Map item effect to weapon effect
 */
public final class EffectToWeaponMapping implements EffectMapper<WeaponEffect> {
    /**
     * Create the effect from the template
     */
    public WeaponEffect create(ItemTemplateEffectEntry entry) {
        return new WeaponEffect(entry.effect(), entry.min(), entry.max(), entry.special());
    }

    @Override
    public WeaponEffect create(ItemTemplateEffectEntry effect, boolean maximize) {
        return create(effect);
    }

    @Override
    public List<WeaponEffect> create(List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return create(effects);
    }

    @Override
    public List<WeaponEffect> create(List<ItemTemplateEffectEntry> effects) {
        return effects
            .stream()
            .filter(e -> e.effect().type() == Effect.Type.WEAPON)
            .map(this::create)
            .collect(Collectors.toList())
        ;
    }

    @Override
    public Class<WeaponEffect> type() {
        return WeaponEffect.class;
    }
}

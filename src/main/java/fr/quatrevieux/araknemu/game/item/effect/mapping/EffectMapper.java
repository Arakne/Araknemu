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

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;

import java.util.List;

/**
 * Mapper for create item effects
 *
 * @param <E> The effect type
 */
public interface EffectMapper<E extends ItemEffect> {
    /**
     * Create one item effect
     *
     * @param effect The template effect
     * @param maximize Maximize effect ?
     */
    public E create(ItemTemplateEffectEntry effect, boolean maximize);

    /**
     * Create the item effect without maximize
     *
     * @param effect The template effect
     */
    public default E create(ItemTemplateEffectEntry effect) {
        return create(effect, false);
    }

    /**
     * Create list of item effects
     *
     * @param effects The template effects
     * @param maximize Maximize effects ?
     */
    public List<E> create(List<ItemTemplateEffectEntry> effects, boolean maximize);

    /**
     * Create list of item effects without maximize effects
     *
     * @param effects The template effects
     */
    public default List<E> create(List<ItemTemplateEffectEntry> effects) {
        return create(effects, false);
    }

    /**
     * Get the mapped item effect class
     */
    public Class<E> type();
}

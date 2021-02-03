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

import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for effect mappers
 */
public final class EffectMappers {
    private final Map<Class<? extends ItemEffect>, EffectMapper> mappers = new HashMap<>();

    public EffectMappers(EffectMapper... mappers) {
        for (EffectMapper mapper : mappers) {
            register(mapper);
        }
    }

    /**
     * Get mapper for the requested item effect type
     *
     * @param type The requested item type
     */
    @SuppressWarnings("unchecked")
    public <E extends ItemEffect> EffectMapper<E> get(Class<E> type) {
        return mappers.get(type);
    }

    @SuppressWarnings("unchecked")
    private void register(EffectMapper mapper) {
        mappers.put(mapper.type(), mapper);
    }
}

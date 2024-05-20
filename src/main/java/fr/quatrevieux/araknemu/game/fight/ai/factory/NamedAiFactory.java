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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;

/**
 * Base type for create AI which can be resolved by name
 *
 * @param <F> The fighter type
 */
public interface NamedAiFactory<F extends ActiveFighter> extends AiFactory<F> {
    /**
     * Get the name of the AI
     *
     * This value must be constant, unique, and case-insensitive.
     * It should use only alphanumeric characters and underscores.
     * To be compatible with entity, it must be at most 12 characters long.
     *
     * By default, will return the class name in uppercase.
     *
     * @return A non empty string
     */
    public default String name() {
        return getClass().getSimpleName().toUpperCase();
    }
}

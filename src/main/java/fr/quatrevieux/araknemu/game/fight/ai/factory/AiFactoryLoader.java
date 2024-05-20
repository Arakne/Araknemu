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
 * Base type for load AI factories
 * This type is used by {@link AggregateAiFactory}
 *
 * @param <F> Fighter type
 */
public interface AiFactoryLoader<F extends ActiveFighter> {
    /**
     * Load factories
     */
    public Iterable<NamedAiFactory<F>> load();

    /**
     * Does the loader should be used lazily?
     *
     * If true, the loader is called only when the requested AI is not found.
     * If false, the loader is called once at the creation of the AggregateAiFactory.
     *
     * Returning true is useful to allow loading of newly added AI factories without restarting the server.
     */
    public boolean lazy();
}

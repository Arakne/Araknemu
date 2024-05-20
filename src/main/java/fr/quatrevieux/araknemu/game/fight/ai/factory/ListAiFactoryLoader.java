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

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of AI loader using a simple list of factories
 *
 * @param <F> Fighter type
 */
public final class ListAiFactoryLoader<F extends ActiveFighter> implements AiFactoryLoader<F> {
    private final List<NamedAiFactory<F>> factories;

    public ListAiFactoryLoader(NamedAiFactory<F>... factories) {
        this.factories = Arrays.asList(factories);
    }

    @Override
    public Iterable<NamedAiFactory<F>> load() {
        return factories;
    }

    @Override
    public boolean lazy() {
        return false;
    }
}

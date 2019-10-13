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

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Aggregate loaders
 */
final public class AggregateLoader implements Loader {
    final private Loader[] loaders;

    public AggregateLoader(Loader... loaders) {
        this.loaders = loaders;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        Collection<PacketHandler<GameSession, ?>> handlers = new ArrayList<>();

        for (Loader loader : loaders) {
            handlers.addAll(
                Arrays.asList(loader.load(container))
            );
        }

        return handlers.toArray(new PacketHandler[0]);
    }
}

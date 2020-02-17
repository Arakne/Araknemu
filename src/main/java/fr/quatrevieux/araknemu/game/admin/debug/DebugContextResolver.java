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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

/**
 * Resolver for debug context
 */
final public class DebugContextResolver implements ContextResolver {
    final private Container container;

    public DebugContextResolver(Container container) {
        this.container = container;
    }

    @Override
    public Context resolve(Context globalContext, Object argument) throws ContextException {
        try {
            return new DebugContext(container, globalContext);
        } catch (ContainerException e) {
            throw new ContextException(e);
        }
    }

    @Override
    public String type() {
        return "debug";
    }
}

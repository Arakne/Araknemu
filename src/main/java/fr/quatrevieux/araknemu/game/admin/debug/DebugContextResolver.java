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

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Resolver for debug context
 */
public final class DebugContextResolver implements ContextResolver {
    private final Context parentContext;

    private final List<AbstractContextConfigurator<DebugContext>> configurators = new ArrayList<>();

    public DebugContextResolver(Context parentContext) {
        this.parentContext = parentContext;
    }

    @Override
    public Context resolve(AdminPerformer performer, Supplier<String> argument) {
        return new DebugContext(parentContext, configurators);
    }

    @Override
    public char prefix() {
        return ':';
    }

    /**
     * Register a configurator for the debug context
     */
    public DebugContextResolver register(AbstractContextConfigurator<DebugContext> configurator) {
        configurators.add(configurator);

        return this;
    }
}

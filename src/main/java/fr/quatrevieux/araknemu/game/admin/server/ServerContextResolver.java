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

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ConfigurableContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Resolve the server context
 */
public final class ServerContextResolver implements ConfigurableContextResolver<ServerContext> {
    private final Context parentContext;

    private final List<AbstractContextConfigurator<ServerContext>> configurators = new ArrayList<>();

    public ServerContextResolver(Context parentContext) {
        this.parentContext = parentContext;
    }

    @Override
    public Context resolve(AdminPerformer performer, Supplier<String> argument) {
        return new ServerContext(parentContext, configurators);
    }

    @Override
    public char prefix() {
        return '*';
    }

    @Override
    public ServerContextResolver register(AbstractContextConfigurator<ServerContext> configurator) {
        configurators.add(configurator);

        return this;
    }
}

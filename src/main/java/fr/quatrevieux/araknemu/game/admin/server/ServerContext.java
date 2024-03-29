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

import fr.quatrevieux.araknemu.game.admin.context.AbstractContext;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.SimpleContext;

import java.util.List;

/**
 * Context for server management
 */
public final class ServerContext extends AbstractContext<ServerContext> {
    private final Context baseContext;

    public ServerContext(Context baseContext, List<AbstractContextConfigurator<ServerContext>> contextConfigurators) {
        super(contextConfigurators);

        this.baseContext = baseContext;
    }

    @Override
    protected SimpleContext createContext() {
        return new SimpleContext(baseContext);
    }
}

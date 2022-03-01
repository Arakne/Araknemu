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

package fr.quatrevieux.araknemu.game.admin.context;

import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.Collection;
import java.util.List;

/**
 * Base context implementation for handle external configuration using {@link AbstractContextConfigurator}
 *
 * @param <C> The implementation class
 */
public abstract class AbstractContext<C extends AbstractContext> implements Context {
    private final List<AbstractContextConfigurator<C>> configurators;
    private @MonotonicNonNull SimpleContext context;

    public AbstractContext(List<AbstractContextConfigurator<C>> configurators) {
        this.configurators = configurators;
    }

    /**
     * Create the base context
     */
    protected abstract SimpleContext createContext();

    @Override
    public final Command command(String name) throws CommandNotFoundException {
        return context().command(name);
    }

    @Override
    public final Collection<Command> commands() {
        return context().commands();
    }

    @Override
    public final Context child(String name) throws ContextNotFoundException {
        return context().child(name);
    }

    /**
     * Get the initialized context
     */
    private Context context() {
        if (context != null) {
            return context;
        }

        context = createContext();

        for (AbstractContextConfigurator<C> configurator : configurators) {
            configurator.with(context).configure((C) this);
        }

        return context;
    }
}

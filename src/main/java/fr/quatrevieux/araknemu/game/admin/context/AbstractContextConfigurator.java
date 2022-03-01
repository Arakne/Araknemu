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
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Configure a context by adding commands and sub-contexts
 * This class must be extended on a module
 */
public abstract class AbstractContextConfigurator<C extends Context> implements Cloneable {
    private @MonotonicNonNull SimpleContext context;

    /**
     * Define the context to be configured
     */
    @SuppressWarnings("unchecked")
    public final AbstractContextConfigurator<C> with(SimpleContext context) {
        AbstractContextConfigurator<C> withContext = null;

        try {
            withContext = (AbstractContextConfigurator<C>) clone();
            withContext.context = context;
        } catch (CloneNotSupportedException e) {
            // Ignore: ContextConfigurator is Cloneable
        }

        return NullnessUtil.castNonNull(withContext);
    }

    /**
     * Configure the context
     */
    public abstract void configure(C context);

    /**
     * Add a new command to the context
     */
    protected final void add(Command<?> command) {
        NullnessUtil.castNonNull(context).add(command);
    }

    /**
     * Add a new child context
     *
     * @param name The child name
     * @param child The child
     */
    protected final void add(String name, Context child) {
        NullnessUtil.castNonNull(context).add(name, child);
    }
}

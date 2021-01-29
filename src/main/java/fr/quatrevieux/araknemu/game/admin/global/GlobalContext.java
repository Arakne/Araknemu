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

package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.game.admin.context.AbstractContext;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.context.SimpleContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Global context.
 * This context should be available in all other contexts
 */
final public class GlobalContext extends AbstractContext<GlobalContext> {
    final private List<AbstractContextConfigurator<GlobalContext>> configurators;

    public GlobalContext(List<AbstractContextConfigurator<GlobalContext>> configurators) {
        super(configurators);

        this.configurators = configurators;
    }

    public GlobalContext() {
        this(new ArrayList<>());
    }

    /**
     * Register a new configurator for the context
     */
    public GlobalContext register(AbstractContextConfigurator<GlobalContext> configurator) {
        configurators.add(configurator);

        return this;
    }

    @Override
    protected SimpleContext createContext() {
        return new SimpleContext(new NullContext())
            .add(new Echo())
            .add(new Help())
        ;
    }
}

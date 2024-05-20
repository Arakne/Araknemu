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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.script;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.Instantiator;
import fr.quatrevieux.araknemu.core.scripting.ScriptLoader;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Load commands from groovy scripts
 *
 * @param <C> The context type
 */
public final class ScriptLoaderContextConfigurator<C extends Context> extends AbstractContextConfigurator<C> {
    private final ScriptLoader loader;
    private final Path path;
    private final Function<C, Container> containerResolver;
    private final Logger logger;

    public ScriptLoaderContextConfigurator(ScriptLoader loader, Path path, Function<C, Container> containerResolver, Logger logger) {
        this.loader = loader;
        this.path = path;
        this.containerResolver = containerResolver;
        this.logger = logger;
    }

    @Override
    public void configure(C context) {
        if (!Files.isDirectory(path)) {
            return;
        }

        final Container container = containerResolver.apply(context);
        final Instantiator instantiator = container.instantiator();
        final ScriptLoader engine = loader.withInstantiator(instantiator);

        for (Command<?> command : engine.loadDirectory(path, Command.class)) {
            logger.debug("Find command {}", command.getClass().getSimpleName());
            add(command);
        }
    }
}

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
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import groovy.util.GroovyScriptEngine;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Load commands from groovy scripts
 *
 * @param <C> The context type
 */
public final class ScriptLoaderContextConfigurator<C extends Context> extends AbstractContextConfigurator<C> {
    private final Path path;
    private final Function<C, Container> containerResolver;
    private final Logger logger;

    private final GroovyScriptEngine engine;

    public ScriptLoaderContextConfigurator(Path path, Function<C, Container> containerResolver, Logger logger) {
        this.path = path;
        this.containerResolver = containerResolver;
        this.logger = logger;

        try {
            engine = new GroovyScriptEngine(new URL[] {path.toUri().toURL()});
        } catch (MalformedURLException e) {
            // Should not occurs
            throw new RuntimeException();
        }
    }

    @Override
    public void configure(C context) {
        if (!Files.isDirectory(path)) {
            return;
        }

        final Container container = containerResolver.apply(context);

        try {
            Files.list(path).forEach(file -> {
                logger.debug("Load command script {}", file.toAbsolutePath().toString());

                try {
                    final Class type = engine.loadScriptByName(file.getFileName().toString());

                    if (Command.class.isAssignableFrom(type)) {
                        logger.debug("Find command {}", type.getSimpleName());
                        add(instantiate(container, type));
                    }
                } catch (Exception e) {
                    logger.error("Fail to load command script", e);
                }
            });
        } catch (IOException e) {
            logger.error("Fail to open commands scripts directory", e);
        }
    }

    private Command instantiate(Container container, Class<? extends Command> type) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (Constructor constructor : type.getConstructors()) {
            final Object[] parameters = resolveArguments(container, constructor);

            if (parameters != null) {
                return (Command) constructor.newInstance(parameters);
            }
        }

        // No constructor found : try to instantiate without constructor
        return type.newInstance();
    }

    private Object[] resolveArguments(Container container, Constructor constructor) {
        final Object[] parameters = new Object[constructor.getParameterCount()];
        final Class[] parametersTypes = constructor.getParameterTypes();

        for (int i = 0; i < parameters.length; ++i) {
            if (!container.has(parametersTypes[i])) {
                return null;
            }

            parameters[i] = container.get(parametersTypes[i]);
        }

        return parameters;
    }
}

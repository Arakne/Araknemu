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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.scripting;

import fr.quatrevieux.araknemu.core.di.Instantiator;
import groovy.util.GroovyScriptEngine;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Load groovy scripts from files or directories
 */
public final class ScriptLoader {
    private final GroovyScriptEngine engine;
    private final Instantiator instantiator;
    private final Logger logger;

    public ScriptLoader(GroovyScriptEngine engine, Instantiator instantiator, Logger logger) {
        this.engine = engine;
        this.instantiator = instantiator;
        this.logger = logger;
    }

    /**
     * @param root The path of the directory storing the scripts, which will be used as root package, so subdirectories will be considered as subpackages.
     * @throws MalformedURLException when the file passed as argument is invalid
     */
    public ScriptLoader(Path root, Instantiator instantiator, Logger logger) throws MalformedURLException {
        this(new GroovyScriptEngine(new URL[] { root.toUri().toURL() }), instantiator, logger);
    }

    /**
     * Change the instantiator used to instantiate classes
     *
     * Note: a new instance of ScriptLoader is returned, the original instance is not modified.
     *
     * @param instantiator New instantiator
     * @return A new instance of ScriptLoader with the given instantiator
     */
    public ScriptLoader withInstantiator(Instantiator instantiator) {
        return new ScriptLoader(engine, instantiator, logger);
    }

    /**
     * Try to load all scripts stored into the given directory,
     * and instantiate classes that match the given type.
     *
     * If the directory is empty, an empty list will be returned and nothing will be performed.
     * All errors will be logged and the script will be skipped. So if the directory cannot be opened, an empty list will be returned.
     *
     * Note: scripts which defines a class that do not match the given type will be compiled but not instantiated.
     *
     * @param path Directory path
     * @param type The type of classes to instantiate
     *
     * @return List of all instantiated classes
     * @param <T> The type of classes to instantiate
     */
    public <T> Collection<T> loadDirectory(Path path, Class<T> type) {
        return baseLoadDirectory(path, type, this::load);
    }

    /**
     * Try to load all scripts stored into the given directory with hot reload capabilities,
     * and instantiate classes that match the given type.
     *
     * To enable hot reload, a proxy will be created for each class, implementing {@link HotReloadableScript},
     * in addition to the given type. So the returned instances will not have access to the original class (e.g. reflection cannot be used directly).
     *
     * It's advised to use this method only in development environment, as it may have a performance impact.
     *
     * If the directory is empty, an empty list will be returned and nothing will be performed.
     * All errors will be logged and the script will be skipped. So if the directory cannot be opened, an empty list will be returned.
     *
     * Note: scripts which defines a class that do not match the given type will be compiled but not instantiated.
     *
     * @param path Directory path
     * @param type The type of classes to instantiate
     *
     * @return List of all instantiated classes
     * @param <T> The type of classes to instantiate
     */
    public <T> Collection<T> loadDirectoryHotReloadable(Path path, Class<T> type) {
        return baseLoadDirectory(path, type, this::loadHotReloadable);
    }

    /**
     * Try to load a single script file class, and instantiate it if it matches the given type.
     * If the type do not match, null is returned.
     *
     * Note: this method will recompile the script each time it is called.
     *
     * @param file Script file to load. Should be located in a subdirectory of the root directory passed to the constructor.
     * @param type The type of class to instantiate
     *
     * @return The instance, or null if the class do not match the given type
     * @param <T> The type of class to instantiate
     *
     * @throws Exception if the script cannot be loaded, compiled or instantiated
     */
    public <T> @Nullable T load(Path file, Class<T> type) throws Exception {
        final Path absolutePath = file.toAbsolutePath();
        logger.debug("Load script {}", absolutePath.toString());

        final Class<?> loadedType = engine.loadScriptByName(absolutePath.toString());

        if (!type.isAssignableFrom(loadedType)) {
            return null;
        }

        return (T) instantiator.instantiate(loadedType);
    }

    /**
     * Try to load a single script file class with hot reload capabilities, and instantiate it if it matches the given type.
     * If the type do not match, null is returned.
     *
     * To enable hot reload, a proxy will be created for each class, implementing {@link HotReloadableScript},
     * in addition to the given type. So the returned instances will not have access to the original class (e.g. reflection cannot be used directly).
     *
     * On each call of a method of the instance, the script will be reloaded if it has been modified.
     * If the new script no longer match the given type, or compile, the last valid version will be kept.
     *
     * It's advised to use this method only in development environment, as it may have a performance impact.
     *
     * Note: Because the instance is recreated on the fly, you cannot store instance variables in the script class.
     *       So, script class should be stateless to allow hot reload.
     *
     * @param file Script file to load. Should be located in a subdirectory of the root directory passed to the constructor.
     * @param type The type of class to instantiate
     *
     * @return The instance, or null if the class do not match the given type
     * @param <T> The type of class to instantiate
     *
     * @throws Exception if the script cannot be loaded, compiled or instantiated
     */
    public <T> @Nullable T loadHotReloadable(Path file, Class<T> type) throws Exception {
        final @Nullable T inner = load(file, type);

        if (inner == null) {
            return null;
        }

        return new HotReloadProxy<>(this, type, logger, file, inner).instantiate();
    }

    private <T> Collection<T> baseLoadDirectory(Path path, Class<T> type, LoadScriptFunction func) {
        if (!Files.isDirectory(path)) {
            return Collections.emptyList();
        }

        final Collection<T> instances = new ArrayList<>();

        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(file -> {
                try {
                    final @Nullable T instance = func.load(file, type);

                    if (instance != null) {
                        instances.add(instance);
                    }
                } catch (Exception e) {
                    logger.error("Fail to load script " + file.toAbsolutePath(), e);
                }
            });
        } catch (IOException e) {
            logger.error("Fail to open scripts directory", e);

            return Collections.emptyList();
        }

        return instances;
    }

    @FunctionalInterface
    private interface LoadScriptFunction {
        public <T> @Nullable T load(Path file, Class<T> type) throws Exception;
    }
}

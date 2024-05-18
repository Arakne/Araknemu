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

import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * Handle proxy creation to enable hot reload of scripts
 *
 * @param <T> The type of the script class
 */
public final class HotReloadProxy<@NonNull T> implements InvocationHandler, HotReloadableScript<T> {
    private final ScriptLoader loader;
    private final Logger logger;
    private final Class<T> type;
    private final Path file;
    private @NonNull T inner;
    private FileTime lastModified;

    HotReloadProxy(ScriptLoader loader, Class<T> type, Logger logger, Path file, @NonNull T inner) throws IOException {
        this.loader = loader;
        this.logger = logger;
        this.type = type;
        this.file = file;
        this.inner = inner;
        this.lastModified = Files.getLastModifiedTime(file);
    }

    @Override
    @SuppressWarnings("override.return")
    public @Nullable Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().isAssignableFrom(HotReloadableScript.class)) {
            return method.invoke(this, args);
        }

        return method.invoke(load(), args);
    }

    @Override
    public T getInternalInstance() {
        return inner;
    }

    @Override
    public Path getScriptFile() {
        return file;
    }

    @Override
    public Class<T> getScriptType() {
        return type;
    }

    /**
     * Create the proxy instance
     */
    public @NonNull T instantiate() {
        return type.cast(Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class[] {
                type,
                HotReloadableScript.class,
            },
            this
        ));
    }

    private @NonNull T load() {
        try {
            final FileTime newModified = Files.getLastModifiedTime(file);

            if (!newModified.equals(lastModified)) {
                logger.debug("The script {} has been modified. Reload it.", file);

                final T newInner = loader.load(file, type);

                if (newInner != null) {
                    inner = newInner;
                    lastModified = newModified;
                } else {
                    logger.error("The new version of the script {} is not compatible with type {}. Keep last version.", file, type);
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to reload script " + file + ". Keep last version.", e);
        }

        return inner;
    }
}

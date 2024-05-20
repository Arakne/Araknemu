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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

/**
 * Mark script classes that are hot-reloadable using {@link HotReloadProxy}
 * This interface is automatically implemented by the proxy
 */
public interface HotReloadableScript<@NonNull T> {
    /**
     * Get the actual instance of the script class
     *
     * Use this method if you want to ignore hot-reloading, and accessing to actual implemented interfaces
     * (e.g. use instanceof or cast)
     *
     * Note: this method will not perform hot-reloading even if the script has been modified
     */
    public T getInternalInstance();

    /**
     * Get the path of the script file which is watched for hot-reloading
     *
     * Note: this file may not exist if the script has been deleted after loading
     */
    public Path getScriptFile();

    /**
     * Get the type of the script class, which is used for implementation of the proxy
     */
    public Class<T> getScriptType();
}

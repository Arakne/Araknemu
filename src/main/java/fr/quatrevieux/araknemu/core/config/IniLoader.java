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

package fr.quatrevieux.araknemu.core.config;

import org.ini4j.Ini;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

/**
 * Loader for ini driver
 */
public final class IniLoader implements ConfigurationLoader.FileLoader {
    private static final PathMatcher extensionMatcher = FileSystems.getDefault().getPathMatcher("regex:.*\\.ini(\\.dist)?$");

    @Override
    public boolean supports(Path path) {
        return extensionMatcher.matches(path);
    }

    @Override
    public Driver load(Path path) throws IOException {
        return new IniDriver(new Ini(path.toFile()));
    }
}

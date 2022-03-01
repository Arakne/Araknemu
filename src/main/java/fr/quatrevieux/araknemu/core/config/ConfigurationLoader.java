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

import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loader for configuration system
 */
public final class ConfigurationLoader {
    private static final String[] defaultConfigFiles = new String[] {"config.ini", "config.ini.dist"};

    private final List<FileLoader> loaders = new ArrayList<>();
    private final Path baseDirectory;

    private @MonotonicNonNull Path configFile;

    public ConfigurationLoader(Path baseDirectory, FileLoader[] loaders) {
        this.baseDirectory = baseDirectory;

        for (FileLoader loader : loaders) {
            register(loader);
        }
    }

    public ConfigurationLoader(Path baseDirectory) {
        this(baseDirectory, new FileLoader[] {
            new IniLoader(),
        });
    }

    public ConfigurationLoader() {
        this(Paths.get(""));
    }

    /**
     * Define the configuration file name to load
     *
     * @param configFileName The file name
     *
     * @return the current instance
     */
    public ConfigurationLoader configFileName(String configFileName) {
        return configFile(Paths.get(configFileName));
    }

    /**
     * Define the configuration file path to load
     *
     * @param configFile The file path (may be relative or absolute)
     *
     * @return the current instance
     */
    public ConfigurationLoader configFile(Path configFile) {
        this.configFile = configFile;

        return this;
    }

    /**
     * Register a new config file loader
     *
     * @param loader The file loader
     */
    @RequiresNonNull("loaders")
    public void register(@UnknownInitialization ConfigurationLoader this, FileLoader loader) {
        loaders.add(loader);
    }

    /**
     * Load the configuration file
     *
     * @return The loaded configuration
     *
     * @throws IOException When cannot load the config file
     */
    public Configuration load() throws IOException {
        if (configFile != null) {
            final Path toLoad = configFile.isAbsolute() ? configFile : baseDirectory.resolve(configFile);

            return load(toLoad).orElseThrow(() -> new IllegalArgumentException("The configurable file " + toLoad.toAbsolutePath() + " cannot be found or cannot be loaded"));
        }

        for (String configFile : defaultConfigFiles) {
            final Optional<Configuration> loaded = load(baseDirectory.resolve(configFile));

            if (loaded.isPresent()) {
                return loaded.get();
            }
        }

        throw new IllegalArgumentException("Cannot found any valid configuration file on directory " + baseDirectory.toAbsolutePath() + ". Please create the file " + baseDirectory.resolve(defaultConfigFiles[0]));
    }

    private Optional<Configuration> load(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }

        for (FileLoader loader : loaders) {
            if (loader.supports(file)) {
                return Optional.of(new DefaultConfiguration(loader.load(file)));
            }
        }

        return Optional.empty();
    }

    /**
     * Loader for a configuration file
     */
    public interface FileLoader {
        /**
         * Check if the loader supports the given file
         * The file extension should be checked here
         *
         * @param path The config file path
         *
         * @return true if supported
         */
        public boolean supports(Path path);

        /**
         * Load the config driver from the file
         *
         * @param path The config file path
         *
         * @return The driver instance
         *
         * @throws IOException When cannot load the configuration file
         */
        public Driver load(Path path) throws IOException;
    }
}

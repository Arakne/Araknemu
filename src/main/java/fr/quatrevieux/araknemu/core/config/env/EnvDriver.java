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

package fr.quatrevieux.araknemu.core.config.env;

import fr.quatrevieux.araknemu.core.config.Driver;
import fr.quatrevieux.araknemu.core.config.Pool;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Decorate a generic driver to handle environment variables
 * and bash style string interpolation.
 */
public final class EnvDriver implements Driver {
    private final Driver driver;
    private final Dotenv dotenv;

    public EnvDriver(Driver driver, Dotenv dotenv) {
        this.driver = driver;
        this.dotenv = dotenv;
    }

    @Override
    public boolean has(String key) {
        return driver.has(key);
    }

    @Override
    public Object get(String key) {
        return driver.get(key);
    }

    @Override
    public Pool pool(String key) {
        return new EnvPool(
            driver.pool(key),
            dotenv
        );
    }
}

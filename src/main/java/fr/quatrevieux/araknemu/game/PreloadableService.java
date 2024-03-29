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

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.InitializableService;
import org.apache.logging.log4j.Logger;

/**
 * Interface for service which can be preloaded on boot
 */
public interface PreloadableService extends InitializableService {
    @Override
    public default void init(Logger logger) {}

    /**
     * Preload the service
     * This method is only called if preload is enabled for the current service
     */
    public default void preload(Logger logger) {}

    /**
     * Get the service name
     * This name is used for enable or not the preloading
     */
    public String name();
}

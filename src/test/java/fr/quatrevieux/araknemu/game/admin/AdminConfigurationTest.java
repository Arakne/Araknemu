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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminConfigurationTest extends GameBaseCase {
    private AdminConfiguration configuration;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        configuration = app.configuration().module(AdminConfiguration.class);
    }

    @Test
    void contextDefaults() {
        removeConfigValue("admin", "debug.scripts.enable");

        assertTrue(configuration.context("debug").enableScripts());
        assertEquals("scripts/commands/debug", configuration.context("debug").scriptsPath());
    }

    @Test
    void contextConfigured() {
        setConfigValue("admin", "debug.scripts.enable", "false");
        setConfigValue("admin", "debug.scripts.path", "my/custom/path");

        assertFalse(configuration.context("debug").enableScripts());
        assertEquals("my/custom/path", configuration.context("debug").scriptsPath());
    }
}

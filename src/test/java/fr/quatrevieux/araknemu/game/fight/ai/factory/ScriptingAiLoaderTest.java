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

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.core.scripting.HotReloadableScript;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.scripting.ScriptingAiLoader;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptingAiLoaderTest extends GameBaseCase {
    private ScriptingAiLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new ScriptingAiLoader(Paths.get("src/test/scripts/ai"), container.instantiator(), Mockito.mock(Logger.class), false);
    }

    @Test
    void load() throws NoSuchFieldException, IllegalAccessException {
        assertTrue(loader.lazy());
        List<NamedAiFactory> factories = toList(loader.load());

        assertCount(2, factories);
        assertEquals("NOOP", factories.get(0).name());
        assertEquals("Noop", factories.get(0).getClass().getName());
        assertEquals("WITHDEP", factories.get(1).name());
        assertEquals("WithDep", factories.get(1).getClass().getName());

        Field f = factories.get(1).getClass().getDeclaredField("simulator");
        assertSame(container.get(Simulator.class), f.get(factories.get(1)));
    }

    @Test
    void loadWithHotReload() throws NoSuchFieldException, IllegalAccessException, MalformedURLException {
        loader = new ScriptingAiLoader(Paths.get("src/test/scripts/ai"), container.instantiator(), Mockito.mock(Logger.class), true);

        assertTrue(loader.lazy());
        List<NamedAiFactory> factories = toList(loader.load());

        assertCount(2, factories);
        assertEquals("NOOP", factories.get(0).name());
        assertInstanceOf(HotReloadableScript.class, factories.get(0));
        assertEquals("WITHDEP", factories.get(1).name());
        assertInstanceOf(HotReloadableScript.class, factories.get(1));

        Field f = HotReloadableScript.class.cast(factories.get(1)).getInternalInstance().getClass().getDeclaredField("simulator");
        assertSame(container.get(Simulator.class), f.get(HotReloadableScript.class.cast(factories.get(1)).getInternalInstance()));
    }

    private <T> List<T> toList(Iterable<T> i) {
        return StreamSupport.stream(i.spliterator(), false).collect(Collectors.toList());
    }
}

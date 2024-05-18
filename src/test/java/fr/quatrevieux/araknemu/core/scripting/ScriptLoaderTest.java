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

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.InstantiatorException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import groovy.lang.GroovyRuntimeException;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptLoaderTest {
    private ScriptLoader loader;
    private Container container;
    private Logger logger;

    @BeforeEach
    void setUp() throws MalformedURLException {
        logger = Mockito.mock(Logger.class);
        container = new ItemPoolContainer();
        loader = new ScriptLoader(
            Paths.get("src/test/scripts/root"),
            container.instantiator(),
            logger
        );
    }

    @Test
    public void loadScriptSimple() throws Exception {
        ScriptInterface script = loader.load(Paths.get("src/test/scripts/root/Simple.groovy"), ScriptInterface.class);

        assertInstanceOf(ScriptInterface.class, script);
        assertEquals(42, script.get());
        assertEquals("Simple", script.getClass().getName());

        Method method = script.getClass().getMethod("hello");
        assertEquals("Hello World !", method.invoke(script));
    }

    @Test
    public void loadBadInterface() throws Exception {
        assertNull(loader.load(Paths.get("src/test/scripts/root/BadInterface.groovy"), ScriptInterface.class));
        assertNull(loader.loadHotReloadable(Paths.get("src/test/scripts/root/BadInterface.groovy"), ScriptInterface.class));
    }

    @Test
    public void loadCompilationError() throws Exception {
        assertThrows(GroovyRuntimeException.class, () -> loader.load(Paths.get("src/test/scripts/root/CompilationError.groovy"), ScriptInterface.class));
        assertThrows(GroovyRuntimeException.class, () -> loader.loadHotReloadable(Paths.get("src/test/scripts/root/CompilationError.groovy"), ScriptInterface.class));
    }

    @Test
    public void loadCannotInstantiate() throws Exception {
        assertThrows(InstantiatorException.class, () -> loader.load(Paths.get("src/test/scripts/root/CannotInstantiate.groovy"), ScriptInterface.class));
        assertThrows(InstantiatorException.class, () -> loader.loadHotReloadable(Paths.get("src/test/scripts/root/CannotInstantiate.groovy"), ScriptInterface.class));
    }

    @Test
    void loadWithDependency() throws Exception {
        container.register(c -> c.set(new Config(21)));

        ScriptInterface script = loader.load(Paths.get("src/test/scripts/root/WithDep.groovy"), ScriptInterface.class);

        assertEquals(20, script.get());
        assertEquals(23, script.get());
    }

    @Test
    void withInstantiator() throws Exception {
        container.register(c -> c.set(new Config(21)));

        ScriptInterface script = loader.load(Paths.get("src/test/scripts/root/WithDep.groovy"), ScriptInterface.class);
        assertEquals(20, script.get());

        ScriptLoader newLoader = loader.withInstantiator(container.with(new Config(42)).instantiator());
        script = newLoader.load(Paths.get("src/test/scripts/root/WithDep.groovy"), ScriptInterface.class);
        assertEquals(43, script.get());
    }

    @Test
    void loadDirectory() {
        Collection<ScriptInterface> scripts = loader.loadDirectory(Paths.get("src/test/scripts/root/multiple"), ScriptInterface.class);

        assertEquals(3, scripts.size());

        Mockito.verify(logger).debug("Load script {}", Paths.get("src/test/scripts/root/multiple/BadInterface.groovy").toAbsolutePath().toString());
        Mockito.verify(logger).debug("Load script {}", Paths.get("src/test/scripts/root/multiple/Bar.groovy").toAbsolutePath().toString());
        Mockito.verify(logger).debug("Load script {}", Paths.get("src/test/scripts/root/multiple/Baz.groovy").toAbsolutePath().toString());
        Mockito.verify(logger).debug("Load script {}", Paths.get("src/test/scripts/root/multiple/CompilationError.groovy").toAbsolutePath().toString());
        Mockito.verify(logger).debug("Load script {}", Paths.get("src/test/scripts/root/multiple/Foo.groovy").toAbsolutePath().toString());

        Mockito.verify(logger).error(Mockito.matches("Fail to load script .*/CompilationError\\.groovy"), Mockito.any(GroovyRuntimeException.class));
    }

    @Test
    void loadDirectoryNotFound() {
        assertTrue(loader.loadDirectory(Paths.get("/not/found"), ScriptInterface.class).isEmpty());
        assertTrue(loader.loadDirectory(Paths.get("/root"), ScriptInterface.class).isEmpty());
    }

    @Test
    void loadHotReloadable() throws Exception {
        Files.copy(Paths.get("src/test/scripts/root/hot-reload/Simple.groovy"), Paths.get("/tmp/Simple.groovy"), StandardCopyOption.REPLACE_EXISTING);

        ScriptInterface script = loader.loadHotReloadable(Paths.get("/tmp/Simple.groovy"), ScriptInterface.class);

        assertInstanceOf(ScriptInterface.class, script);
        assertInstanceOf(HotReloadableScript.class, script);
        assertEquals(42, script.get());

        HotReloadableScript hotReloadableScript = (HotReloadableScript) script;
        assertEquals("/tmp/Simple.groovy", hotReloadableScript.getScriptFile().toAbsolutePath().toString());
        assertEquals(ScriptInterface.class, hotReloadableScript.getScriptType());
        assertEquals("Simple", hotReloadableScript.getInternalInstance().getClass().getName());
        assertEquals("Hello World !", hotReloadableScript.getInternalInstance().getClass().getMethod("hello").invoke(hotReloadableScript.getInternalInstance()));

        Files.copy(Paths.get("src/test/scripts/root/hot-reload/Simple2.groovy"), Paths.get("/tmp/Simple.groovy"), StandardCopyOption.REPLACE_EXISTING);
        Thread.sleep(110);

        assertEquals(28, script.get());
        assertEquals("updated", hotReloadableScript.getInternalInstance().getClass().getMethod("hello").invoke(hotReloadableScript.getInternalInstance()));
        assertEquals("new", hotReloadableScript.getInternalInstance().getClass().getMethod("newMethod").invoke(hotReloadableScript.getInternalInstance()));
        Mockito.verify(logger).debug("The script {} has been modified. Reload it.", Paths.get("/tmp/Simple.groovy"));
        Mockito.clearInvocations(logger);

        Files.copy(Paths.get("src/test/scripts/root/hot-reload/Simple3.groovy"), Paths.get("/tmp/Simple.groovy"), StandardCopyOption.REPLACE_EXISTING);
        Thread.sleep(110); // Groovy has a refresh delay, so wait to be sure the file is reloaded

        assertEquals(28, script.get());
        Mockito.verify(logger).debug("The script {} has been modified. Reload it.", Paths.get("/tmp/Simple.groovy"));
        Mockito.verify(logger).error("The new version of the script {} is not compatible with type {}. Keep last version.", Paths.get("/tmp/Simple.groovy"), ScriptInterface.class);
        Mockito.clearInvocations(logger);

        Files.copy(Paths.get("src/test/scripts/root/hot-reload/Simple4.groovy"), Paths.get("/tmp/Simple.groovy"), StandardCopyOption.REPLACE_EXISTING);
        Thread.sleep(110); // Groovy has a refresh delay, so wait to be sure the file is reloaded

        assertEquals(28, script.get());
        Mockito.verify(logger).debug("The script {} has been modified. Reload it.", Paths.get("/tmp/Simple.groovy"));
        Mockito.verify(logger).error(Mockito.eq("Failed to reload script /tmp/Simple.groovy. Keep last version."), Mockito.any(GroovyRuntimeException.class));
    }

    @Test
    void loadDirectoryHotReloadable() throws Exception {
        if (!Files.isDirectory(Paths.get("/tmp/hot-reload"))) {
            Files.createDirectory(Paths.get("/tmp/hot-reload"));
        }

        Files.copy(Paths.get("src/test/scripts/root/hot-reload/Simple.groovy"), Paths.get("/tmp/hot-reload/Simple.groovy"), StandardCopyOption.REPLACE_EXISTING);

        Collection<ScriptInterface> scripts = loader.loadDirectoryHotReloadable(Paths.get("/tmp/hot-reload"), ScriptInterface.class);

        assertEquals(1, scripts.size());

        assertTrue(scripts.stream().anyMatch(s -> s instanceof HotReloadableScript));
        assertEquals(42, scripts.stream().mapToInt(ScriptInterface::get).sum());

        Files.copy(Paths.get("src/test/scripts/root/hot-reload/Simple2.groovy"), Paths.get("/tmp/hot-reload/Simple.groovy"), StandardCopyOption.REPLACE_EXISTING);
        Thread.sleep(110);
        assertEquals(28, scripts.stream().mapToInt(ScriptInterface::get).sum());
    }

    public static interface ScriptInterface {
        public int get();
    }

    public static interface OtherInterface {
        public int get();
    }

    public static class Config {
        public final int value;

        public Config(int value) {
            this.value = value;
        }
    }
}

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

package fr.quatrevieux.araknemu.game.fight.ai.factory.scripting;

import fr.quatrevieux.araknemu.core.di.Instantiator;
import fr.quatrevieux.araknemu.core.scripting.ScriptLoader;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactoryLoader;
import fr.quatrevieux.araknemu.game.fight.ai.factory.NamedAiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Load AI factories from groovy script directory
 *
 * @param <F> Fighter type
 */
public final class ScriptingAiLoader<F extends ActiveFighter> implements AiFactoryLoader<F> {
    private final Path path;
    private final ScriptLoader loader;
    private final boolean hotReload;

    public ScriptingAiLoader(Path path, Instantiator instantiator, Logger logger, boolean hotReload) throws MalformedURLException {
        this.path = path;
        this.loader = new ScriptLoader(path, instantiator, logger);
        this.hotReload = hotReload;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<NamedAiFactory<F>> load() {
        final Collection<NamedAiFactory> factories = hotReload
            ? loader.loadDirectoryHotReloadable(path, NamedAiFactory.class)
            : loader.loadDirectory(path, NamedAiFactory.class)
        ;

        return (Collection) factories;
    }

    @Override
    public boolean lazy() {
        return true;
    }
}

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

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Aggregate multiple AI factories and select the right by its name
 *
 * @param <F> Supported fighter type
 */
public final class AggregateAiFactory<F extends ActiveFighter> implements AiFactory<F> {
    private final Map<String, NamedAiFactory<F>> factories = new HashMap<>();
    private final Function<F, @Nullable String> aiNameResolver;
    private final List<AiFactoryLoader<F>> lazyLoaders = new ArrayList<>();

    /**
     * @param loaders List of AI factories loaders
     * @param aiNameResolver Resolve the AI name from the fighter. Return null if the fighter has no AI.
     */
    public AggregateAiFactory(Iterable<AiFactoryLoader<F>> loaders, Function<F, @Nullable String> aiNameResolver) {
        this.aiNameResolver = aiNameResolver;

        for (AiFactoryLoader<F> loader : loaders) {
            if (loader.lazy()) {
                lazyLoaders.add(loader);
            } else {
                load(loader);
            }
        }
    }

    @Override
    public Optional<AI> create(F fighter) {
        final String aiName = aiNameResolver.apply(fighter);

        if (aiName == null) {
            return Optional.empty();
        }

        return create(fighter, aiName);
    }

    /**
     * Try to create the AI for the given fighter
     *
     * @param fighter The fighter
     * @param name The AI type name
     *
     * @return The AI instance wrapped in an Optional
     * @throws IllegalArgumentException If the AI type is not supported
     */
    public Optional<AI> create(F fighter, String name) {
        NamedAiFactory<F> factory = factories.get(name.toUpperCase());

        if (factory == null) {
            for (AiFactoryLoader<F> loader : lazyLoaders) {
                load(loader);

                factory = factories.get(name.toUpperCase());

                if (factory != null) {
                    break;
                }
            }
        }

        if (factory == null) {
            throw new IllegalArgumentException("Unsupported AI type " + name);
        }

        return factory.create(fighter);
    }

    private void load(AiFactoryLoader<F> loader) {
        for (NamedAiFactory<F> factory : loader.load()) {
            factories.put(factory.name().toUpperCase(), factory);
        }
    }
}

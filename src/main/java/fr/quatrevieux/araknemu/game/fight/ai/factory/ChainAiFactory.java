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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Chain AI factories
 */
public final class ChainAiFactory implements AiFactory<PlayableFighter> {
    private final List<AiFactory<PlayableFighter>> factories;

    @SafeVarargs
    public ChainAiFactory(AiFactory<PlayableFighter>... factories) {
        this.factories = Arrays.asList(factories);
    }

    @Override
    public Optional<AI<PlayableFighter>> create(PlayableFighter fighter) {
        for (AiFactory<PlayableFighter> factory : factories) {
            final Optional<AI<PlayableFighter>> ai = factory.create(fighter);

            if (ai.isPresent()) {
                return ai;
            }
        }

        return Optional.empty();
    }
}

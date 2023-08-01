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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * AI factory for {@link DoubleFighter}
 */
public final class DoubleAiFactory implements AiFactory<PlayableFighter> {
    private final AiFactory<PlayableFighter> factory;

    public DoubleAiFactory(AiFactory<PlayableFighter> factory) {
        this.factory = factory;
    }

    @Override
    public Optional<AI> create(PlayableFighter fighter) {
        return Optional.ofNullable(fighter.apply(new Resolver()).ai);
    }

    private class Resolver implements FighterOperation {
        private @Nullable AI ai;

        @Override
        public void onDouble(DoubleFighter fighter) {
            factory.create(fighter).ifPresent(ai -> this.ai = ai);
        }
    }
}

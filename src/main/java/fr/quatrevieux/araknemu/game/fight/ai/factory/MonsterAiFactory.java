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
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of AI factories for monsters
 */
public final class MonsterAiFactory implements AiFactory<PlayableFighter> {
    private final Map<String, AiFactory<PlayableFighter>> factories = new HashMap<>();

    public void register(String type, AiFactory<PlayableFighter> factory) {
        factories.put(type, factory);
    }

    @Override
    public Optional<AI<PlayableFighter>> create(PlayableFighter fighter) {
        return fighter.apply(new ResolveAi()).get();
    }

    class ResolveAi implements FighterOperation {
        private @MonotonicNonNull AI<PlayableFighter> ai;

        @Override
        public void onMonster(MonsterFighter fighter) {
            resolve(fighter, fighter.monster().ai());
        }

        @Override
        public void onInvocation(InvocationFighter fighter) {
            resolve(fighter, fighter.monster().ai());
        }

        public Optional<AI<PlayableFighter>> get() {
            return Optional.ofNullable(ai);
        }

        private void resolve(PlayableFighter fighter, String type) {
            final AiFactory<PlayableFighter> factory = factories.get(type);

            if (factory == null) {
                throw new IllegalArgumentException("Unsupported AI type " + type);
            }

            factory
                .create(fighter)
                .ifPresent(ai -> this.ai = ai)
            ;
        }
    }
}

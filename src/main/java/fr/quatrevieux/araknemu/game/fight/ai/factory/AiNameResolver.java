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

import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.monster.Monster;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

/**
 * Extract the AI name from the fighter, by calling {@link Monster#ai()}
 */
public final class AiNameResolver implements Function<PlayableFighter, @Nullable String> {
    @Override
    public @Nullable String apply(PlayableFighter playableFighter) {
        return playableFighter.apply(new FighterOperation() {
            private @Nullable String name = null;

            @Override
            public void onMonster(MonsterFighter fighter) {
                name = fighter.monster().ai();
            }

            @Override
            public void onInvocation(InvocationFighter fighter) {
                name = fighter.monster().ai();
            }
        }).name;
    }
}

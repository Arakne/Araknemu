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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of AI factories for monsters
 */
final public class MonsterAiFactory implements AiFactory {
    final private Map<String, AiFactory> factories = new HashMap<>();

    class ResolveAi implements FighterOperation {
        private AI ai;

        @Override
        public void onMonster(MonsterFighter fighter) {
            factories.get(fighter.monster().ai())
                .create(fighter)
                .ifPresent(ai -> this.ai = ai)
            ;
        }

        public Optional<AI> get() {
            return Optional.ofNullable(ai);
        }
    }

    public void register(String type, AiFactory factory) {
        factories.put(type, factory);
    }

    @Override
    public Optional<AI> create(Fighter fighter) {
        return fighter.apply(new ResolveAi()).get();
    }
}

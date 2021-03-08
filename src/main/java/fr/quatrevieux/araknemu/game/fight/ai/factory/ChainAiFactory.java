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

import java.util.Optional;

/**
 * Chain AI factories
 */
public final class ChainAiFactory implements AiFactory {
    private final AiFactory[] factories;

    public ChainAiFactory(AiFactory... factories) {
        this.factories = factories;
    }

    @Override
    public Optional<AI> create(Fighter fighter) {
        for (AiFactory factory : factories) {
            final Optional<AI> ai = factory.create(fighter);

            if (ai.isPresent()) {
                return ai;
            }
        }

        return Optional.empty();
    }
}

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

package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;

import java.util.EnumMap;
import java.util.Map;

/**
 * Aggregate exchange factories for a given creature type
 *
 * @param <C> The supported creature type
 */
public class ExchangeFactoryAggregate<C extends ExplorationCreature> implements ExchangeFactory<C> {
    final private Map<ExchangeType, ExchangeTypeFactory<C>> factories = new EnumMap<>(ExchangeType.class);

    @SafeVarargs
    public ExchangeFactoryAggregate(ExchangeTypeFactory<C>... factories) {
        for (ExchangeTypeFactory<C> factory : factories) {
            register(factory);
        }
    }

    /**
     * Register a new factory
     */
    final protected void register(ExchangeTypeFactory<C> factory) {
        factories.put(factory.type(), factory);
    }

    @Override
    final public ExchangeInteraction create(ExchangeType type, ExplorationPlayer initiator, C target) {
        if (!factories.containsKey(type)) {
            throw new IllegalArgumentException("Unsupported type " + type);
        }

        return factories.get(type).create(initiator, target);
    }
}

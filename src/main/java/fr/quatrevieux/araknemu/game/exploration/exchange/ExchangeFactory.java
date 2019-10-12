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

/**
 * Base type for creates exchange with a target
 *
 * @param <C> The target creature type
 */
public interface ExchangeFactory<C extends ExplorationCreature> {
    /**
     * Creates an exchange with a creature
     *
     * @param type The exchange type
     * @param initiator The exchange initiator (current player)
     * @param target The target
     *
     * @return The exchange interaction
     *
     * @throws IllegalArgumentException When type or target are invalid
     */
    public ExchangeInteraction create(ExchangeType type, ExplorationPlayer initiator, C target);
}

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

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.Exchange;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;

import java.util.Optional;

/**
 * Provides an exchange for the npc
 */
public interface ExchangeProvider {
    /**
     * Factory for creates an Npc exchange
     */
    public interface Factory {
        /**
         * Creates the exchange
         *
         * @param initiator The player who initiate the exchange
         * @param npc The target npc
         *
         * @return The exchange
         */
        public Exchange create(ExplorationPlayer initiator, GameNpc npc);

        /**
         * The supported exchange type
         */
        public ExchangeType type();
    }

    /**
     * Load the exchange factory, if available
     *
     * @param template The npc template
     */
    public Optional<? extends Factory> load(NpcTemplate template);
}

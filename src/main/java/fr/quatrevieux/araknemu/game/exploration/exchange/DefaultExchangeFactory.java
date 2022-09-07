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
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Factory for exchanges
 */
public final class DefaultExchangeFactory implements ExchangeFactory<ExplorationCreature> {
    private final ExchangeFactory<ExplorationPlayer> playerFactory;
    private final ExchangeFactory<GameNpc> npcFactory;

    public DefaultExchangeFactory(ExchangeFactory<ExplorationPlayer> playerFactory, ExchangeFactory<GameNpc> npcFactory) {
        this.playerFactory = playerFactory;
        this.npcFactory = npcFactory;
    }

    @Override
    public ExchangeInteraction create(ExchangeType type, ExplorationPlayer initiator, ExplorationCreature target) {
        final @Nullable ExchangeInteraction exchange = target.apply(new CreateExchange(type, initiator));

        if (exchange == null) {
            throw new IllegalArgumentException("Bad target");
        }

        return exchange;
    }

    /**
     * Visitor operation for create the exchange on the valid target
     */
    private final class CreateExchange implements Operation<ExchangeInteraction> {
        private final ExchangeType type;
        private final ExplorationPlayer initiator;

        public CreateExchange(ExchangeType type, ExplorationPlayer initiator) {
            this.type = type;
            this.initiator = initiator;
        }

        @Override
        public ExchangeInteraction onExplorationPlayer(ExplorationPlayer player) {
            return playerFactory.create(type, initiator, player);
        }

        @Override
        public ExchangeInteraction onNpc(GameNpc npc) {
            return npcFactory.create(type, initiator, npc);
        }
    }
}

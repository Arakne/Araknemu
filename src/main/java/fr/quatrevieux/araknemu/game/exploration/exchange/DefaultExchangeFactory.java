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

import java.util.Optional;

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

    /**
     * Visitor operation for create the exchange on the valid target
     */
    private final class CreateExchange implements Operation {
        private final ExchangeType type;
        private final ExplorationPlayer initiator;

        private ExchangeInteraction exchange;

        public CreateExchange(ExchangeType type, ExplorationPlayer initiator) {
            this.type = type;
            this.initiator = initiator;
        }

        @Override
        public void onExplorationPlayer(ExplorationPlayer player) {
            exchange = playerFactory.create(type, initiator, player);
        }

        @Override
        public void onNpc(GameNpc npc) {
            exchange = npcFactory.create(type, initiator, npc);
        }

        public Optional<ExchangeInteraction> exchange() {
            return Optional.ofNullable(exchange);
        }
    }

    @Override
    public ExchangeInteraction create(ExchangeType type, ExplorationPlayer initiator, ExplorationCreature target) {
        final CreateExchange operation = new CreateExchange(type, initiator);

        target.apply(operation);

        return operation.exchange().orElseThrow(() -> new IllegalArgumentException("Bad target"));
    }
}

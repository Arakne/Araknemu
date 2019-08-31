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

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeFactoryAggregateTest extends GameBaseCase {
    @Test
    void createInvalidType() {
        ExchangeFactoryAggregate<ExplorationPlayer> factory = new ExchangeFactoryAggregate<>();

        assertThrows(IllegalArgumentException.class, () -> factory.create(ExchangeType.BANK, explorationPlayer(), makeOtherExplorationPlayer()));
    }

    @Test
    void createSuccess() throws Exception {
        ExplorationPlayer initiator = explorationPlayer();
        ExplorationPlayer target = makeOtherExplorationPlayer();
        ExchangeInteraction exchange = Mockito.mock(ExchangeInteraction.class);

        SimpleExchangeTypeFactory<ExplorationPlayer> typeFactory = new SimpleExchangeTypeFactory<>(
            ExchangeType.PLAYER_EXCHANGE,
            (initiator1, target1) -> exchange
        );

        ExchangeFactoryAggregate<ExplorationPlayer> factory = new ExchangeFactoryAggregate<>(typeFactory);

        assertSame(exchange, factory.create(ExchangeType.PLAYER_EXCHANGE, initiator, target));
    }
}

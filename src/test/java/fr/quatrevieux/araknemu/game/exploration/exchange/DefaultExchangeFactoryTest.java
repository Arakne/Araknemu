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
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcExchangeFactories;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultExchangeFactoryTest extends GameBaseCase {
    private DefaultExchangeFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new DefaultExchangeFactory(
            new PlayerExchangeFactories(),
            new NpcExchangeFactories()
        );
    }

    @Test
    void createSuccess() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        ExchangeInteraction interaction = factory.create(ExchangeType.PLAYER_EXCHANGE, player, other);

        assertInstanceOf(PlayerExchangeRequest.class, interaction);
    }

    @Test
    void createInvalidType() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        assertThrows(IllegalArgumentException.class, () -> factory.create(ExchangeType.UNKNOWN_14, player, other));
    }

    @Test
    void createInvalidTarget() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        assertThrows(IllegalArgumentException.class, () -> factory.create(ExchangeType.PLAYER_EXCHANGE, player, Mockito.mock(ExplorationCreature.class)));
    }

    @Test
    void createNpcStore() throws SQLException {
        dataSet.pushNpcWithStore();

        ExplorationPlayer player = explorationPlayer();
        player.join(container.get(ExplorationMapService.class).load(10340));
        ExplorationCreature target = explorationPlayer().map().creature(-1000104);

        assertInstanceOf(StoreDialog.class, factory.create(ExchangeType.NPC_STORE, player, target));
    }
}

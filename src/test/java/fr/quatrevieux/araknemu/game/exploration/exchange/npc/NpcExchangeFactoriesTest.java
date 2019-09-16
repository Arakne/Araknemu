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

package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpcExchangeFactoriesTest extends GameBaseCase {
    private NpcExchangeFactories factories;
    private ExplorationPlayer player;
    private NpcService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushNpcs()
            .pushNpcWithStore()
            .pushNpcExchange(1, 878, 100, "39:2", 0, "2422")
        ;

        service = container.get(NpcService.class);
        factories = new NpcExchangeFactories();
        player = explorationPlayer();
    }

    @Test
    void createNpcStore() {
        GameNpc npc = service.get(10001);
        ExchangeInteraction interaction = factories.create(ExchangeType.NPC_STORE, player, npc);

        assertInstanceOf(StoreDialog.class, interaction);

        player.interactions().start(interaction);
        requestStack.assertOne(new ExchangeCreated(ExchangeType.NPC_STORE, npc));
    }

    @Test
    void createNpcExchange() {
        GameNpc npc = service.get(472);
        ExchangeInteraction interaction = factories.create(ExchangeType.NPC_EXCHANGE, player, npc);

        assertInstanceOf(ExchangeDialog.class, interaction);
        player.interactions().start(interaction);
        requestStack.assertOne(new ExchangeCreated(ExchangeType.NPC_EXCHANGE, npc));
    }
}

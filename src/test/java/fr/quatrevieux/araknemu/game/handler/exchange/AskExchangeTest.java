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

package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.InitiatorExchangeRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.TargetExchangeRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AskExchangeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private AskExchange handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.changeMap(player.map(), 123);

        handler = new AskExchange(container.get(ExchangeFactory.class));
    }

    @Test
    void success() throws ErrorPacket {
        handler.handle(session, new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null));

        requestStack.assertLast(new ExchangeRequested(player, other, ExchangeType.PLAYER_EXCHANGE));

        assertInstanceOf(InitiatorExchangeRequestDialog.class, player.interactions().get(Interaction.class));
        assertInstanceOf(TargetExchangeRequestDialog.class, other.interactions().get(Interaction.class));
    }

    @Test
    void notOnMap() throws ErrorPacket {
        player.leave();
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null)));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void successWithNpcStore() throws ErrorPacket, SQLException {
        dataSet.pushNpcWithStore();

        GameNpc npc = container.get(NpcService.class).get(10001);
        player.map().add(npc);

        handler.handle(session, new ExchangeRequest(ExchangeType.NPC_STORE, npc.id(), null));

        requestStack.assertOne(new ExchangeCreated(ExchangeType.NPC_STORE, npc));
        assertInstanceOf(StoreDialog.class, player.interactions().get(Interaction.class));
    }

    @Test
    void invalidTarget() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, -8, null)));
    }

    @Test
    void invalidType() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new ExchangeRequest(ExchangeType.BANK, other.id(), null)));
    }

    @Test
    void notExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null)));
    }

    @Test
    void functional() throws Exception {
        handlePacket(new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null));

        assertInstanceOf(InitiatorExchangeRequestDialog.class, player.interactions().get(Interaction.class));
        assertInstanceOf(TargetExchangeRequestDialog.class, other.interactions().get(Interaction.class));
    }
}

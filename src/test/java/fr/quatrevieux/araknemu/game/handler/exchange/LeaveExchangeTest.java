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
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.exchange.LeaveExchangeRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaveExchangeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.changeMap(player.map(), 123);
    }

    @Test
    void functionalOnRequest() throws Exception {
        player.interactions().start(new PlayerExchangeRequest(player, other));

        handlePacket(new LeaveExchangeRequest());

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void functionalOnExchange() throws Exception {
        for (PlayerExchangeParty party : PlayerExchangeParty.make(player, other)) {
            party.start();
        }

        handlePacket(new LeaveExchangeRequest());

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void functionalNotExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new LeaveExchangeRequest()));
    }
}

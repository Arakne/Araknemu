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

package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class InitiatorExchangeRequestDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private InitiatorExchangeRequestDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.changeMap(player.map(), 123);

        PlayerExchangeRequest request = new PlayerExchangeRequest(player, other);

        dialog = new InitiatorExchangeRequestDialog(request.invitation(player, other));
        player.interactions().start(dialog);
        other.interactions().start(new TargetExchangeRequestDialog(request.invitation(player, other)));
    }

    @Test
    void stop() {
        dialog.stop();

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());
    }

    @Test
    void leave() {
        dialog.leave();

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());
    }
}

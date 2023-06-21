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
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TargetExchangeRequestDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private TargetExchangeRequestDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.changeMap(player.map(), 123);

        PlayerExchangeRequest request = new PlayerExchangeRequest(other, player);

        dialog = new TargetExchangeRequestDialog(request.invitation(other, player));
        player.interactions().start(dialog);
        other.interactions().start(new InitiatorExchangeRequestDialog(request.invitation(other, player)));
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

    @Test
    void accept() {
        dialog.accept();

        requestStack.assertLast(new ExchangeCreated(ExchangeType.PLAYER_EXCHANGE));

        assertInstanceOf(ExchangeDialog.class, player.interactions().get(Interaction.class));
        assertInstanceOf(ExchangeDialog.class, other.interactions().get(Interaction.class));
    }
}

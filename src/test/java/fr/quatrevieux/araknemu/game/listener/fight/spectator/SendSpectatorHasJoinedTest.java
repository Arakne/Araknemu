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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorJoined;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendSpectatorHasJoinedTest extends FightBaseCase {
    private Fight fight;
    private SendSpectatorHasJoined listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new SendSpectatorHasJoined(fight);
    }

    @Test
    void onSpectatorJoined() throws SQLException {
        listener.on(new SpectatorJoined(new Spectator(makeSimpleGamePlayer(5), fight)));

        requestStack.assertLast(Information.spectatorHasJoinFight("PLAYER_5"));
    }

    @Test
    void functional() throws SQLException {
        fight.nextState();
        fight.spectators().add(new Spectator(makeSimpleGamePlayer(5), fight));

        requestStack.assertLast(Information.spectatorHasJoinFight("PLAYER_5"));
    }
}

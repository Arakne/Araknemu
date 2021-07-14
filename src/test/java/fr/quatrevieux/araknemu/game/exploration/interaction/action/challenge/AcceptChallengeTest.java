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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitationHandler;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptChallengeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = new ExplorationPlayer(makeOtherPlayer());

        player.changeMap(container.get(ExplorationMapService.class).load(10340), 123);
        other.changeMap(player.map(), 123);
    }

    @Test
    void noInvitation() throws SQLException, ContainerException {
        AcceptChallenge action = new AcceptChallenge(explorationPlayer(), 5);

        assertThrows(IllegalArgumentException.class, () -> action.start(new ActionQueue()), "Invalid interaction type");
    }

    @Test
    void badTarget() throws Exception {
        other.interactions().start(invitationHandler().invitation(other, player));

        AcceptChallenge action = new AcceptChallenge(explorationPlayer(), -5);

        assertThrows(IllegalArgumentException.class, () -> action.start(new ActionQueue()), "Invalid challenge target");
    }

    @Test
    void initiatorCannotAccept() throws Exception {
        explorationPlayer().interactions().start(invitationHandler().invitation(player, other));

        AcceptChallenge action = new AcceptChallenge(player, player.id());

        assertThrows(IllegalArgumentException.class, () -> action.start(new ActionQueue()));
    }

    @Test
    void successFromChallenger() {
        other.interactions().start(invitationHandler().invitation(other, player));

        AcceptChallenge action = new AcceptChallenge(player, other.id());

        action.start(new ActionQueue());

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());

        requestStack.assertOne(new GameActionResponse("", ActionType.ACCEPT_CHALLENGE, player.id(), other.id()));
        requestStack.assertOne(new JoinFight(player.player().fighter().fight()));
    }

    private ChallengeInvitationHandler invitationHandler() {
        return new ChallengeInvitationHandler(container.get(FightService.class).handler(ChallengeBuilder.class));
    }
}

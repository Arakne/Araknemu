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

package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeInvitationHandlerTest extends GameBaseCase {
    private ChallengeInvitationHandler handler;
    private ExplorationPlayer initiator;
    private ExplorationPlayer challenger;
    private Invitation invitation;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        initiator = explorationPlayer();
        challenger = new ExplorationPlayer(makeOtherPlayer());

        initiator.leave();
        initiator.changeMap(container.get(ExplorationMapService.class).load(10340), 123);
        challenger.changeMap(initiator.map(), 123);

        handler = new ChallengeInvitationHandler(container.get(FightService.class).handler(ChallengeBuilder.class));
        invitation = handler.invitation(initiator, challenger);
    }

    @Test
    void getters() {
        assertSame(initiator, invitation.initiator());
        assertSame(challenger, invitation.target());
    }

    @Test
    void startInitiatorBusy() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        initiator.interactions().start(interaction);

        assertNull(invitation.start());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "o")
        );
    }

    @Test
    void startChallengerBusy() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        challenger.interactions().start(interaction);

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "z")
        );
    }

    @Test
    void startInitiatorCantChallenge() {
        initiator.player().restrictions().set(Restrictions.Restriction.DENY_CHALLENGE);

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "i")
        );
    }

    @Test
    void startChallengerCantChallenge() {
        challenger.player().restrictions().set(Restrictions.Restriction.DENY_CHALLENGE);
        challenger.restrictions().refresh();

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "i")
        );
    }

    @Test
    void startNotOnSameMap() throws ContainerException {
        challenger.changeMap(
            container.get(ExplorationMapService.class).load(10540),
            123
        );

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "p")
        );
    }

    @Test
    void startNotOnMap() throws ContainerException {
        initiator.leave();

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "p")
        );
    }

    @Test
    void startCannotLaunchFightOnMap() throws ContainerException {
        initiator.changeMap(container.get(ExplorationMapService.class).load(10300), 132);
        challenger.changeMap(container.get(ExplorationMapService.class).load(10300), 132);

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, initiator.id(), "p")
        );
    }

    @Test
    void startSuccess() {
        Interaction interaction = invitation.start();

        assertInstanceOf(InitiatorDialog.class, interaction);
        assertTrue(challenger.interactions().busy());
        assertInstanceOf(ChallengerDialog.class, challenger.interactions().get(Interaction.class));

        requestStack.assertLast(
            new GameActionResponse("", ActionType.CHALLENGE, initiator.id(), challenger.id())
        );
    }

    @Test
    void stop() {
        initiator.interactions().start(invitation);
        invitation.stop();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());
    }

    @Test
    void acceptInitiatorLeaveMap() {
        initiator.interactions().start(invitation);

        initiator.leave();
        invitation.accept(challenger.interactions().get(ChallengerDialog.class));

        requestStack.assertLast(new GameActionResponse("", ActionType.REFUSE_CHALLENGE, challenger.id(), initiator.id()));

        assertFalse(initiator.interactions().busy());
        assertFalse(challenger.interactions().busy());
    }
}

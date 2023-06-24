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

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengerDialogTest extends GameBaseCase {
    private ExplorationPlayer initiator;
    private ExplorationPlayer challenger;
    private Invitation invitation;
    private ChallengerDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        initiator = explorationPlayer();
        challenger = makeOtherExplorationPlayer();

        initiator.changeMap(container.get(ExplorationMapService.class).load(10340), 123);
        challenger.changeMap(initiator.map(), 123);

        invitation = new ChallengeInvitationHandler(container.get(FightService.class).handler(ChallengeBuilder.class)).invitation(initiator, challenger);
        initiator.interactions().start(invitation);

        dialog = new ChallengerDialog(invitation);
    }

    @Test
    void self() {
        assertSame(challenger, dialog.self());
    }

    @Test
    void interlocutor() {
        assertSame(initiator, dialog.interlocutor());
    }

    @Test
    void stop() {
        dialog.stop();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, challenger.id(), initiator.id())
        );
    }

    @Test
    void decline() {
        dialog.decline();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, challenger.id(), initiator.id())
        );
    }

    @Test
    void accept() {
        ExplorationMap map = initiator.map();

        dialog.accept();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        assertTrue(initiator.player().isFighting());
        assertFalse(initiator.player().isExploring());
        assertTrue(challenger.player().isFighting());
        assertFalse(challenger.player().isExploring());

        assertFalse(map.creatures().contains(initiator));
        assertFalse(map.creatures().contains(challenger));

        assertInstanceOf(ChallengeType.class, initiator.player().fighter().fight().type());
        assertInstanceOf(PlacementState.class, initiator.player().fighter().fight().state());

        requestStack.assertOne(new GameActionResponse("", ActionType.ACCEPT_CHALLENGE, challenger.id(), initiator.id()));
        requestStack.assertOne(new JoinFight(session.fighter().fight()));
    }
}

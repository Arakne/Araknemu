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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.*;
import fr.quatrevieux.araknemu.game.listener.fight.turn.*;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightAction;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightActionTerminated;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ActiveStateTest extends GameBaseCase {
    private ActiveState state;
    private Fight fight;
    private PlayerFighter fighter;
    private PlayerFighter other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        fight = new Fight(
            1,
            new ChallengeType(),
            container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            Arrays.asList(
                new SimpleTeam(fighter = new PlayerFighter(gamePlayer(true)), Arrays.asList(123, 222), 0),
                new SimpleTeam(other = new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            ),
            new StatesFlow(
                new NullState(),
                new PlacementState(false),
                state = new ActiveState()
            ),
            NOPLogger.NOP_LOGGER
        );

        fight.nextState();
        requestStack.clear();
    }

    @RepeatedIfExceptionsTest
    void start() throws InterruptedException {
        state.start(fight);

        assertTrue(fight.dispatcher().has(SendFightStarted.class));
        assertTrue(fight.dispatcher().has(SendFightersInformation.class));
        assertTrue(fight.dispatcher().has(SendFightTurnStarted.class));
        assertTrue(fight.dispatcher().has(SendFightTurnStopped.class));
        assertTrue(fight.dispatcher().has(SendFightAction.class));
        assertTrue(fight.dispatcher().has(SendFightActionTerminated.class));
        assertTrue(fight.dispatcher().has(SendUsedMovementPoints.class));
        assertTrue(fight.dispatcher().has(SendUsedActionPoints.class));
        assertTrue(fight.dispatcher().has(SendFighterLifeChanged.class));
        assertTrue(fight.dispatcher().has(SendFighterDie.class));
        assertTrue(fight.dispatcher().has(RemoveDeadFighter.class));
        assertTrue(fight.dispatcher().has(CheckFightTerminated.class));
        assertTrue(fight.dispatcher().has(SendTurnList.class));
        assertTrue(fight.dispatcher().has(RefreshBuffs.class));

        Thread.sleep(210); // Wait for start turn

        assertTrue(fight.turnList().current().isPresent());

        requestStack.assertAll(
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get())
        );
    }

    @Test
    void terminateSuccess() {
        fight.nextState();
        state.terminate();

        assertFalse(fight.dispatcher().has(SendFightStarted.class));
        assertFalse(fight.dispatcher().has(SendFightersInformation.class));
        assertFalse(fight.dispatcher().has(SendFightTurnStarted.class));
        assertFalse(fight.dispatcher().has(SendFightTurnStopped.class));
        assertFalse(fight.dispatcher().has(SendFightAction.class));
        assertFalse(fight.dispatcher().has(SendFightActionTerminated.class));
        assertFalse(fight.dispatcher().has(SendUsedMovementPoints.class));
        assertFalse(fight.dispatcher().has(SendUsedActionPoints.class));
        assertFalse(fight.dispatcher().has(SendFighterLifeChanged.class));
        assertFalse(fight.dispatcher().has(SendFighterDie.class));
        assertFalse(fight.dispatcher().has(RemoveDeadFighter.class));
        assertFalse(fight.dispatcher().has(CheckFightTerminated.class));
        assertFalse(fight.dispatcher().has(SendTurnList.class));
        assertFalse(fight.dispatcher().has(RefreshBuffs.class));

        assertFalse(fight.turnList().current().isPresent());
        assertFalse(fight.active());
    }

    @Test
    void terminateBadState() {
        state.start(fight);
        state.terminate();
    }

    @Test
    void leaveFightLastOfTeamWillTerminateTheFight() {
        fight.nextState();

        state.leave(other);

        assertTrue(other.dead());
        assertFalse(fight.active());
        assertContains(other, fight.fighters());

        requestStack.assertLast(ActionEffect.fighterDie(other, other));
    }

    @Test
    void leaveNotLastOfTeamWillKillAndRemoveTheFighterFromFight() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter mutineer = new PlayerFighter(makeSimpleGamePlayer(10));

        fight.team(0).join(mutineer);
        mutineer.joinFight(fight, fight.map().get(222));

        fight.nextState();
        requestStack.clear();

        state.leave(mutineer);

        assertTrue(fight.active());
        assertFalse(mutineer.cell().fighter().isPresent());
        assertTrue(mutineer.dead());
        assertFalse(fight.team(0).fighters().contains(mutineer));
        assertFalse(fight.fighters().contains(mutineer));
        assertFalse(fight.turnList().fighters().contains(mutineer));

        requestStack.assertAll(
            ActionEffect.fighterDie(mutineer, mutineer),
            new FighterTurnOrder(fight.turnList())
        );
    }

    @Test
    void leaveNotLastOfTeamWillDispatchLeavedEventWithALooserReward() throws SQLException, ContainerException, JoinFightException {
        AtomicReference<FightLeaved> ref = new AtomicReference<>();
        fighter.dispatcher().add(FightLeaved.class, ref::set);

        PlayerFighter teammate = new PlayerFighter(makeSimpleGamePlayer(10));

        fight.team(0).join(teammate);
        teammate.joinFight(fight, fight.map().get(222));

        fight.nextState();
        requestStack.clear();

        state.leave(fighter);

        assertNotNull(ref.get());
        assertInstanceOf(DropReward.class, ref.get().reward().get());
        assertEquals(RewardType.LOOSER, ref.get().reward().get().type());
    }
}

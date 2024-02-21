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
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.DispelDeadFighterBuff;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.KillInvocationsOnInvokerDie;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RefreshBuffs;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RemoveDeadFighter;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RemoveDeadInvocationFromTurnList;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterDie;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterHidden;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterLifeChanged;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterMaxLifeChanged;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterVisible;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStarted;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStopped;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightersInformation;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendTurnList;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendUsedActionPoints;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendUsedMovementPoints;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightAction;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightActionTerminated;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import fr.quatrevieux.araknemu.util.ExecutorFactory;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActiveStateTest extends FightBaseCase {
    private ActiveState state;
    private Fight fight;
    private PlayerFighter fighter;
    private PlayerFighter other;
    private ScheduledExecutorService executor;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        final GamePlayer me = gamePlayer(true);
        final GamePlayer enemy = super.other;

        FightMap map;
        fight = new Fight(
            1,
            new ChallengeType(configuration.fight()),
            map = container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            Arrays.asList(
                fight -> new SimpleTeam(fight, fighter = container.get(FighterFactory.class).create(me), Arrays.asList(map.get(123), map.get(222)), 0),
                fight -> new SimpleTeam(fight, other = new PlayerFighter(enemy), Arrays.asList(map.get(321)), 1)
            ),
            new StatesFlow(
                new NullState(),
                new PlacementState(false),
                state = new ActiveState()
            ),
            container.get(Logger.class),
            executor = ExecutorFactory.createSingleThread(),
            container.get(ActionsFactory.Factory.class)
        );

        fight.nextState();
        requestStack.clear();
    }

    @Override
    @AfterEach
    public void tearDown() throws ContainerException {
        executor.shutdownNow();

        super.tearDown();
    }

    @Test
    void notStarted() {
        assertThrows(IllegalStateException.class, state::listeners);
        assertThrows(IllegalStateException.class, () -> state.leave(fighter));
        state.terminate();
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
        assertTrue(fight.dispatcher().has(DispelDeadFighterBuff.class));
        assertTrue(fight.dispatcher().has(CheckFightTerminated.class));
        assertTrue(fight.dispatcher().has(SendTurnList.class));
        assertTrue(fight.dispatcher().has(RefreshBuffs.class));
        assertTrue(fight.dispatcher().has(SendFighterMaxLifeChanged.class));
        assertTrue(fight.dispatcher().has(SendFighterVisible.class));
        assertTrue(fight.dispatcher().has(SendFighterHidden.class));
        assertTrue(fight.dispatcher().has(RemoveDeadInvocationFromTurnList.class));
        assertTrue(fight.dispatcher().has(KillInvocationsOnInvokerDie.class));

        Thread.sleep(210); // Wait for start turn

        assertTrue(fight.turnList().current().isPresent());

        requestStack.assertAll(
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new Stats(fighter.properties()),
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
        assertFalse(fight.dispatcher().has(SendFighterMaxLifeChanged.class));
        assertFalse(fight.dispatcher().has(SendFighterVisible.class));
        assertFalse(fight.dispatcher().has(SendFighterHidden.class));
        assertFalse(fight.dispatcher().has(RemoveDeadInvocationFromTurnList.class));
        assertFalse(fight.dispatcher().has(KillInvocationsOnInvokerDie.class));

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
        assertContains(other, fight.fighters().all());

        requestStack.assertLast(ActionEffect.fighterDie(other, other));
    }

    @Test
    void leaveFightCancelledShouldDoNothing() {
        fight.nextState();
        state.terminate();

        state.leave(other);

        assertFalse(other.dead());
        assertContains(other, fight.fighters().all());
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
        assertFalse(fight.map().get(222).hasFighter());
        assertTrue(mutineer.dead());
        assertFalse(fight.team(0).fighters().contains(mutineer));
        assertFalse(fight.fighters().all().contains(mutineer));
        assertFalse(fight.turnList().fighters().contains(mutineer));

        requestStack.assertAll(
            ActionEffect.fighterDie(mutineer, mutineer),
            new FighterTurnOrder(fight.turnList()),
            new RemoveSprite(mutineer.sprite())
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

    @Test
    void leavePvmFightShouldApplyLooseReward() throws Exception {
        AtomicReference<FightLeaved> ref = new AtomicReference<>();
        PlayerFighter teammate = new PlayerFighter(makeSimpleGamePlayer(10));

        fight = createPvmFight();
        fight.state(PlacementState.class).joinTeam(
            teammate,
            player.fighter().team()
        );
        player.fighter().dispatcher().add(FightLeaved.class, ref::set);

        state.start(fight);
        state.leave(player.fighter());

        assertNotNull(ref.get());
        assertInstanceOf(DropReward.class, ref.get().reward().get());
        assertEquals(RewardType.LOOSER, ref.get().reward().get().type());
        assertEquals(0, gamePlayer().properties().life().current(), 2); // Add a delta to ensure that life regeneration will fail the test
    }
}

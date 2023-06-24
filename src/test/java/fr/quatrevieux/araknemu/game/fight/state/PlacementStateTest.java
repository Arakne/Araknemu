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
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterPositions;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterReadyState;
import fr.quatrevieux.araknemu.game.listener.fight.SendJoinTeamOptionChangedMessage;
import fr.quatrevieux.araknemu.game.listener.fight.SendNeedHelpOptionChangedMessage;
import fr.quatrevieux.araknemu.game.listener.fight.SendNewFighter;
import fr.quatrevieux.araknemu.game.listener.fight.StartFightWhenAllReady;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.ClearFighter;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterRemoved;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import fr.quatrevieux.araknemu.util.ExecutorFactory;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementStateTest extends FightBaseCase {
    private Fight fight;
    private PlacementState state;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        FightMap map;
        fight = new Fight(
            1,
            new ChallengeType(configuration.fight()),
            map = loadFightMap(10340),
            new ArrayList<>(Arrays.asList(
                fight -> new SimpleTeam(fight, fighter = makePlayerFighter(player), Arrays.asList(map.get(123), map.get(222)), 0),
                fight -> new SimpleTeam(fight, makePlayerFighter(other), Arrays.asList(map.get(321)), 1)
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                state = new PlacementState(false),
                new ActiveState()
            ),
            container.get(Logger.class),
            ExecutorFactory.createSingleThread(),
            container.get(ActionsFactory.Factory.class)
        );
    }

    @Test
    void notStarted() {
        assertThrows(IllegalStateException.class, state::listeners);
        assertThrows(IllegalStateException.class, state::remainingTime);
        assertThrows(JoinFightException.class, () -> state.joinTeam(fighter, fight.team(0)));
        assertThrows(InvalidFightStateException.class, () -> state.leave(fighter));

        state.changePlace(fighter, fight.map().get(0));
    }

    @Test
    void remainingTimeNotSupported() {
        state.start(fight);
        assertThrows(UnsupportedOperationException.class, () -> state.remainingTime());
    }

    @Test
    void start() {
        AtomicReference<FightJoined> ref = new AtomicReference<>();
        PlayerFighter.class.cast(new ArrayList<>(fight.team(0).fighters()).get(0)).dispatcher().add(FightJoined.class, ref::set);

        state.start(fight);

        assertNotNull(ref.get());
        assertSame(fight, ref.get().fight());
        assertSame(fighter, ref.get().fighter());

        assertTrue(fight.dispatcher().has(SendFighterPositions.class));
        assertTrue(fight.dispatcher().has(SendFighterReadyState.class));
        assertTrue(fight.dispatcher().has(StartFightWhenAllReady.class));
        assertTrue(fight.dispatcher().has(SendJoinTeamOptionChangedMessage.class));
        assertTrue(fight.dispatcher().has(SendNeedHelpOptionChangedMessage.class));

        assertEquals(123, fighter.cell().id());
        assertEquals(fight.team(0), fighter.team());
        assertEquals(fight, fighter.fight());
        assertEquals(321, fight.team(1).leader().cell().id());
        assertEquals(fight.team(1), fight.team(1).leader().team());
        assertEquals(fight, fight.team(1).leader().fight());

        assertCount(2, fight.fighters().all());
    }

    @Test
    void startRandomized() {
        state = new PlacementState(true);

        state.start(fight);

        assertNotNull(fighter.cell());
        assertEquals(fight.team(0), fighter.team());
        assertEquals(fight, fighter.fight());
        assertNotNull(fight.team(1).leader().cell());
        assertEquals(fight.team(1), fight.team(1).leader().team());
        assertEquals(fight, fight.team(1).leader().fight());
    }

    @RepeatedIfExceptionsTest
    void startWithPlacementTimeLimitShouldStartsFightOnTimeOut() throws InterruptedException {
        FightType type = Mockito.mock(FightType.class);

        Mockito.when(type.hasPlacementTimeLimit()).thenReturn(true);
        Mockito.when(type.placementDuration()).thenReturn(Duration.ZERO);

        FightMap map;
        fight = new Fight(
            1,
            type,
            map = loadFightMap(10340),
            new ArrayList<>(Arrays.asList(
                fight -> new SimpleTeam(fight, fighter = makePlayerFighter(player), Arrays.asList(map.get(123), map.get(222)), 0),
                fight -> new SimpleTeam(fight, makePlayerFighter(other), Arrays.asList(map.get(321)), 1)
            )),
            new StatesFlow(
                state = new PlacementState(false),
                new ActiveState()
            ),
            container.get(Logger.class),
            ExecutorFactory.createSingleThread(),
            container.get(ActionsFactory.Factory.class)
        );

        state.start(fight);
        Thread.sleep(100);

        assertInstanceOf(ActiveState.class, fight.state());
    }

    @Test
    void startWithPlacementTimeLimitShouldCancelTimerOnStartFight() throws NoSuchFieldException, IllegalAccessException {
        FightType type = Mockito.mock(FightType.class);

        Mockito.when(type.hasPlacementTimeLimit()).thenReturn(true);
        Mockito.when(type.placementDuration()).thenReturn(Duration.ofSeconds(10));

        FightMap map;
        fight = new Fight(
            1,
            type,
            map = loadFightMap(10340),
            new ArrayList<>(Arrays.asList(
                fight -> new SimpleTeam(fight, fighter = makePlayerFighter(player), Arrays.asList(map.get(123), map.get(222)), 0),
                fight -> new SimpleTeam(fight, makePlayerFighter(other), Arrays.asList(map.get(321)), 1)
            )),
            new StatesFlow(
                state = new PlacementState(false),
                new ActiveState()
            ),
            container.get(Logger.class),
            ExecutorFactory.createSingleThread(),
            container.get(ActionsFactory.Factory.class)
        );

        state.start(fight);
        state.startFight();

        assertInstanceOf(ActiveState.class, fight.state());

        Field timer = state.getClass().getDeclaredField("timer");
        timer.setAccessible(true);

        assertTrue(((ScheduledFuture) timer.get(state)).isCancelled());
    }

    @Test
    void startWithPlacementTimeLimitShouldCancelTimerOnCancel() throws NoSuchFieldException, IllegalAccessException {
        FightType type = Mockito.mock(FightType.class);

        Mockito.when(type.hasPlacementTimeLimit()).thenReturn(true);
        Mockito.when(type.placementDuration()).thenReturn(Duration.ofSeconds(10));

        FightMap map;
        fight = new Fight(
            1,
            type,
            map = loadFightMap(10340),
            new ArrayList<>(Arrays.asList(
                fight -> new SimpleTeam(fight, fighter = makePlayerFighter(player), Arrays.asList(map.get(123), map.get(222)), 0),
                fight -> new SimpleTeam(fight, makePlayerFighter(other), Arrays.asList(map.get(321)), 1)
            )),
            new StatesFlow(
                state = new PlacementState(false),
                new ActiveState()
            ),
            container.get(Logger.class),
            ExecutorFactory.createSingleThread(),
            container.get(ActionsFactory.Factory.class)
        );

        state.start(fight);
        fight.cancel();
        state.startFight();

        assertSame(state, fight.state());

        Field timer = state.getClass().getDeclaredField("timer");
        timer.setAccessible(true);

        assertTrue(((ScheduledFuture) timer.get(state)).isCancelled());
    }

    @Test
    void changePlaceToNotWalkable() {
        fight.nextState();

        assertThrows(FightMapException.class, () -> state.changePlace(fighter, fight.map().get(0)));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceNotTeamCell() {
        fight.nextState();

        assertThrows(FightException.class, () -> state.changePlace(fighter, fight.map().get(223)));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceFighterReady() {
        fight.nextState();
        fighter.setReady(true);

        assertThrows(FightException.class, () -> state.changePlace(fighter, fight.map().get(222)));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceFightCancelled() {
        fight.nextState();
        FightCell cell = fight.map().get(222);
        fight.cancel();

        state.changePlace(fighter, cell);
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceFightStarted() {
        fight.nextState();
        state.startFight();

        state.changePlace(fighter, fight.map().get(222));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceSuccess() {
        fight.nextState();

        state.changePlace(fighter, fight.map().get(222));

        assertEquals(222, fighter.cell().id());
        assertEquals(fighter, fight.map().get(222).fighter());

        requestStack.assertLast(new FighterPositions(fight.fighters()));
    }

    @Test
    void startFightBadStateDoNothing() {
        state.start(fight);
        state.startFight();

        assertInstanceOf(NullState.class, fight.state());
    }

    @Test
    void startFightCancelledDoNothing() {
        fight.nextState();
        fight.cancel();
        state.startFight();

        assertSame(state, fight.state());
    }

    @Test
    void startFightSuccess() {
        fight.nextState();

        state.startFight();

        assertInstanceOf(ActiveState.class, fight.state());

        assertFalse(fight.dispatcher().has(SendFighterPositions.class));
        assertFalse(fight.dispatcher().has(SendFighterReadyState.class));
        assertFalse(fight.dispatcher().has(StartFightWhenAllReady.class));
        assertFalse(fight.dispatcher().has(SendNewFighter.class));
        assertFalse(fight.dispatcher().has(ClearFighter.class));
        assertFalse(fight.dispatcher().has(SendFighterRemoved.class));
    }

    @Test
    void joinTeamSuccess() throws SQLException, ContainerException, JoinFightException {
        fight.nextState();

        AtomicReference<FighterAdded> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterAdded.class, ref::set);

        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));
        requestStack.clear();

        state.joinTeam(newFighter, fight.team(0));

        assertCount(2, fight.team(0).fighters());
        assertContains(newFighter, fight.team(0).fighters());

        assertSame(fight, newFighter.fight());
        assertSame(fight.team(0), newFighter.team());
        assertNotNull(newFighter.cell());
        assertSame(newFighter, newFighter.cell().fighter());
        assertContains(newFighter.cell(), fight.team(0).startPlaces());

        assertSame(newFighter, ref.get().fighter());

        requestStack.assertLast(new AddSprites(Collections.singleton(newFighter.sprite())));
    }

    @Test
    void joinTeamBadState() throws SQLException, ContainerException {
        fight.nextState();
        state.startFight();

        AtomicReference<FighterAdded> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterAdded.class, ref::set);

        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));

        assertThrows(JoinFightException.class, () -> state.joinTeam(newFighter, fight.team(0)));
        assertNull(ref.get());
        assertCount(1, fight.team(0).fighters());
    }

    @Test
    void leaveBadState() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));

        state.startFight();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        assertThrows(InvalidFightStateException.class, () -> state.leave(newFighter));
        assertNull(ref.get());
        assertContains(newFighter, fight.fighters().all());
    }

    @Test
    void leaveNotLeader() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));
        requestStack.clear();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        state.leave(newFighter);
        assertSame(newFighter, ref.get().fighter());
        assertFalse(fight.fighters().all().contains(newFighter));
        assertFalse(newFighter.cell().hasFighter());

        requestStack.assertLast(new RemoveSprite(newFighter.sprite()));
    }

    @Test
    void leaveLeaderWillDissolveTeam() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));
        requestStack.clear();

        AtomicReference<FightCancelled> ref = new AtomicReference<>();
        fight.dispatcher().add(FightCancelled.class, ref::set);

        state.leave(fighter);

        assertCount(0, fight.fighters().all());
        assertSame(fight, ref.get().fight());
        requestStack.assertLast(new CancelFight());
    }

    @Test
    void leaveNotLeavableShouldPunishDeserter() throws Exception {
        fight = createPvmFight();
        fight.state(PlacementState.class).leave(player.fighter());

        assertEquals(0, player.properties().life().current());
    }

    @Test
    void kickBadState() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));

        state.startFight();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        assertThrows(InvalidFightStateException.class, () -> state.kick(newFighter));
        assertNull(ref.get());
        assertContains(newFighter, fight.fighters().all());
    }

    @Test
    void kickNotLeavableShouldNotPunishDeserter() throws Exception {
        fight = createPvmFight();
        fight.state(PlacementState.class).kick(player.fighter());

        assertEquals(player.properties().life().max(), player.properties().life().current());
    }

    @Test
    void kickSuccess() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = makePlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));
        requestStack.clear();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        state.kick(newFighter);
        assertSame(newFighter, ref.get().fighter());
        assertFalse(fight.fighters().all().contains(newFighter));
        assertFalse(newFighter.cell().hasFighter());

        requestStack.assertLast(new RemoveSprite(newFighter.sprite()));
    }
}

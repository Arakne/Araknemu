package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterPositions;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterReadyState;
import fr.quatrevieux.araknemu.game.listener.fight.SendNewFighter;
import fr.quatrevieux.araknemu.game.listener.fight.StartFightWhenAllReady;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.ClearFighter;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterRemoved;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlacementStateTest extends GameBaseCase {
    private Fight fight;
    private PlacementState state;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fight = new Fight(
            1,
            new ChallengeType(),
            container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            new ArrayList<>(Arrays.asList(
                new SimpleTeam(fighter = new PlayerFighter(gamePlayer(true)), Arrays.asList(123, 222), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            )),
            new StatesFlow(
                new NullState(),
                new NullState(),
                state = new PlacementState(),
                new ActiveState()
            )
        );

        new InitialiseState(false).start(fight);
    }

    @Test
    void start() {
        AtomicReference<FightJoined> ref = new AtomicReference<>();
        PlayerFighter.class.cast(fight.fighters().get(0)).dispatcher().add(FightJoined.class, ref::set);

        state.start(fight);

        assertNotNull(ref.get());
        assertSame(fight, ref.get().fight());
        assertSame(fighter, ref.get().fighter());

        assertTrue(fight.dispatcher().has(SendFighterPositions.class));
        assertTrue(fight.dispatcher().has(SendFighterReadyState.class));
        assertTrue(fight.dispatcher().has(StartFightWhenAllReady.class));
    }

    @Test
    void changePlaceToNotWalkable() {
        state.start(fight);

        assertThrows(FightMapException.class, () -> state.changePlace(fighter, fight.map().get(0)));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceNotTeamCell() {
        state.start(fight);

        assertThrows(FightException.class, () -> state.changePlace(fighter, fight.map().get(223)));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceFighterReady() {
        state.start(fight);
        fighter.setReady(true);

        assertThrows(FightException.class, () -> state.changePlace(fighter, fight.map().get(222)));
        assertEquals(123, fighter.cell().id());
    }

    @Test
    void changePlaceSuccess() throws InterruptedException {
        state.start(fight);

        state.changePlace(fighter, fight.map().get(222));

        assertEquals(222, fighter.cell().id());
        assertEquals(fighter, fight.map().get(222).fighter().get());

        requestStack.assertLast(new FighterPositions(fight.fighters()));
    }

    @Test
    void startFightBadStateDoNothing() {
        state.start(fight);
        state.startFight();

        assertInstanceOf(NullState.class, fight.state());
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

        PlayerFighter newFighter = new PlayerFighter(makeSimpleGamePlayer(5));
        requestStack.clear();

        state.joinTeam(newFighter, fight.team(0));

        assertCount(2, fight.team(0).fighters());
        assertContains(newFighter, fight.team(0).fighters());

        assertSame(fight, newFighter.fight());
        assertSame(fight.team(0), newFighter.team());
        assertNotNull(newFighter.cell());
        assertSame(newFighter, newFighter.cell().fighter().get());
        assertContains(newFighter.cell().id(), fight.team(0).startPlaces());

        assertSame(newFighter, ref.get().fighter());

        requestStack.assertLast(new AddSprites(Collections.singleton(newFighter.sprite())));
    }

    @Test
    void joinTeamBadState() throws SQLException, ContainerException {
        fight.nextState();
        state.startFight();

        AtomicReference<FighterAdded> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterAdded.class, ref::set);

        PlayerFighter newFighter = new PlayerFighter(makeSimpleGamePlayer(5));

        assertThrows(JoinFightException.class, () -> state.joinTeam(newFighter, fight.team(0)));
        assertNull(ref.get());
        assertCount(1, fight.team(0).fighters());
    }

    @Test
    void leaveBadState() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = new PlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));

        state.startFight();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        assertThrows(InvalidFightStateException.class, () -> state.leave(newFighter));
        assertNull(ref.get());
        assertContains(newFighter, fight.fighters());
    }

    @Test
    void leaveNotLeader() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = new PlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));
        requestStack.clear();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        state.leave(newFighter);
        assertSame(newFighter, ref.get().fighter());
        assertFalse(fight.fighters().contains(newFighter));
        assertFalse(newFighter.cell().fighter().isPresent());

        requestStack.assertLast(new RemoveSprite(newFighter.sprite()));
    }

    @Test
    void leaveLeaderWillDissolveTeam() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter newFighter = new PlayerFighter(makeSimpleGamePlayer(5));

        fight.nextState();
        state.joinTeam(newFighter, fight.team(0));
        requestStack.clear();

        AtomicReference<FightCancelled> ref = new AtomicReference<>();
        fight.dispatcher().add(FightCancelled.class, ref::set);

        state.leave(fighter);

        assertCount(0, fight.fighters());
        assertSame(fight, ref.get().fight());
        requestStack.assertLast(new CancelFight());
    }
}

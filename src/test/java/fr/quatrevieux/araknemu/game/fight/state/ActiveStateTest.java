package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RemoveDeadFighter;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterDie;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterLifeChanged;
import fr.quatrevieux.araknemu.game.listener.fight.turn.*;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightAction;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightActionTerminated;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActiveStateTest extends GameBaseCase {
    private ActiveState state;
    private Fight fight;
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
            Arrays.asList(
                new SimpleTeam(fighter = new PlayerFighter(gamePlayer(true)), Arrays.asList(123, 222), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            ),
            new StatesFlow(
                new NullState(),
                new NullState(),
                state = new ActiveState()
            )
        );

        new InitialiseState(false).start(fight);
        requestStack.clear();
    }

    @Test
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

        assertFalse(fight.turnList().current().isPresent());
        assertFalse(fight.active());
    }

    @Test
    void terminateBadState() {
        state.start(fight);
        state.terminate();
    }
}

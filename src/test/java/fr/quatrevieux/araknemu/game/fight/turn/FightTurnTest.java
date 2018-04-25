package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.TurnActionsFactory;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FightTurnTest extends FightBaseCase {
    private FightTurn turn;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.turnList().init(teams -> Arrays.asList(player.fighter(), other.fighter()));
        turn = new FightTurn(player.fighter(), fight, Duration.ofMillis(50));
    }

    @Test
    void getters() {
        assertSame(player.fighter(), turn.fighter());
        assertSame(fight, turn.fight());
        assertEquals(Duration.ofMillis(50), turn.duration());
        assertFalse(turn.active());
        assertInstanceOf(TurnActionsFactory.class, turn.actions());
    }

    @Test
    void stopNotStarted() {
        turn.stop();
        assertFalse(turn.active());
    }

    @Test
    void start() {
        AtomicReference<TurnStarted> ref = new AtomicReference<>();
        fight.dispatcher().add(TurnStarted.class, ref::set);

        turn.start();

        assertSame(turn, ref.get().turn());
        assertTrue(turn.active());
        assertSame(turn, player.fighter().turn());
    }

    @Test
    void startWillInitPoints() {
        turn.start();

        assertInstanceOf(FighterTurnPoints.class, turn.points());
        assertEquals(3, turn.points().movementPoints());
    }

    @Test
    void autoStopOnTimeout() throws InterruptedException {
        turn.start();
        assertTrue(turn.active());

        Thread.sleep(55);
        assertFalse(turn.active());
    }

    @Test
    void stop() {
        turn.start();

        AtomicReference<TurnStopped> ref = new AtomicReference<>();
        fight.dispatcher().add(TurnStopped.class, ref::set);

        turn.stop();

        assertFalse(turn.active());
        assertThrows(FightException.class, () -> player.fighter().turn());
        assertSame(other.fighter(), fight.turnList().current().get().fighter());
    }

    @Test
    void performNotActive() {
        assertThrows(FightException.class, () -> turn.perform(Mockito.mock(Action.class)));
    }

    @Test
    void performInvalidAction() {
        turn.start();

        Action action = Mockito.mock(Action.class);
        Mockito.when(action.validate()).thenReturn(false);

        assertThrows(FightException.class, () -> turn.perform(action));
    }

    @Test
    void performSuccess() {
        turn.start();

        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(result.success()).thenReturn(false);

        turn.perform(action);

        Mockito.verify(action).start();
    }

    @Test
    void terminate() {
        turn.start();

        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofSeconds(30));
        Mockito.when(result.success()).thenReturn(true);

        turn.perform(action);
        turn.terminate();

        Mockito.verify(action).end();
    }

    @Test
    void stopWillWaitForActionTermination() {
        turn.start();

        AtomicReference<TurnStopped> ref = new AtomicReference<>();
        fight.dispatcher().add(TurnStopped.class, ref::set);

        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofSeconds(30));
        Mockito.when(result.success()).thenReturn(true);

        turn.perform(action);
        turn.stop();

        assertNull(ref.get());

        turn.terminate();
        assertSame(turn, ref.get().turn());
    }
}

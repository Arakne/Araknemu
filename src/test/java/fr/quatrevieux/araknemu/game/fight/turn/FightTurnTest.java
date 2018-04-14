package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertEquals(Duration.ofMillis(50), turn.duration());
        assertFalse(turn.active());
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
}

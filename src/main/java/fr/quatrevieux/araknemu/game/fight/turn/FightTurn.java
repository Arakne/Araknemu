package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle a fighter turn
 */
final public class FightTurn {
    final private AtomicBoolean active = new AtomicBoolean(false);

    final private Fighter fighter;
    final private Fight fight;
    final private Duration duration;

    private ScheduledFuture timer;

    public FightTurn(Fighter fighter, Fight fight, Duration duration) {
        this.fighter = fighter;
        this.fight = fight;
        this.duration = duration;
    }

    /**
     * Get the current fighter
     */
    public Fighter fighter() {
        return fighter;
    }

    /**
     * Get the turn duration
     */
    public Duration duration() {
        return duration;
    }

    /**
     * Check if the turn is active
     */
    public boolean active() {
        return active.get();
    }

    /**
     * Start the turn
     *
     * @return true if the turn is successfully started, or false when turn needs to be skipped
     */
    public boolean start() {
        active.set(true);
        fighter.play(this);

        fight.dispatch(new TurnStarted(this));
        timer = fight.schedule(this::stop, duration);

        return true;
    }

    /**
     * Stop the turn and start the next turn
     */
    public void stop() {
        if (!active.getAndSet(false)) {
            return;
        }

        timer.cancel(false);

        fight.dispatch(new TurnStopped(this));
        fighter.stop();

        fight.turnList().next();
    }
}

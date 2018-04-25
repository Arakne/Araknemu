package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionHandler;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.TurnActionsFactory;
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

    final private ActionHandler actionHandler;
    final private FightActionFactory actionFactory;

    private ScheduledFuture timer;
    private FighterTurnPoints points;


    public FightTurn(Fighter fighter, Fight fight, Duration duration) {
        this.fighter = fighter;
        this.fight = fight;
        this.duration = duration;
        this.actionHandler = new ActionHandler(fight);
        this.actionFactory = new TurnActionsFactory(this);
    }

    /**
     * Get the current fighter
     */
    public Fighter fighter() {
        return fighter;
    }

    /**
     * Get the related fight
     */
    public Fight fight() {
        return fight;
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
        fighter.play(this);

        points = new FighterTurnPoints(fight, fighter);

        active.set(true);
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

        actionHandler.terminated(() -> {
            fight.dispatch(new TurnStopped(this));
            fighter.stop();

            fight.turnList().next();
        });
    }

    /**
     * Perform a fight action
     *
     * @param action The action to perform
     */
    public void perform(Action action) throws FightException {
        if (!active.get()) {
            throw new FightException("Turn is not active");
        }

        if (!actionHandler.start(action)) {
            throw new FightException("Cannot start the action");
        }
    }

    /**
     * Terminate the current action
     */
    public void terminate() {
        actionHandler.terminate();
    }

    /**
     * Get the actions factory
     */
    public FightActionFactory actions() {
        return actionFactory;
    }

    /**
     * Get the current fighter points
     */
    public FighterTurnPoints points() {
        return points;
    }
}

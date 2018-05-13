package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

/**
 * Handle fight action
 */
final public class ActionHandler {
    final private Fight fight;

    final private Collection<Runnable> termination = new ArrayList<>();

    private Action current;
    private ScheduledFuture future;

    public ActionHandler(Fight fight) {
        this.fight = fight;
    }

    /**
     * Start a fight action
     *
     * @param action Action to start
     *
     * @return true if the action is successfully started, or false is cannot start the action
     */
    public boolean start(Action action) {
        if (current != null) {
            return false;
        }

        if (!action.validate()) {
            return false;
        }

        ActionResult result = action.start();

        if (result.success()) {
            current = action;
            future = fight.schedule(this::terminate, action.duration());
        }

        fight.dispatch(new FightActionStarted(action, result));

        if (!result.success()) {
            action.failed();
        }

        return true;
    }

    /**
     * Terminate the current action
     */
    public void terminate() {
        if (current == null) {
            return;
        }

        future.cancel(false);

        Action action = current;

        try {
            action.end();
        } finally {
            current = null;
            fight.dispatch(new FightActionTerminated(action));

            termination.forEach(Runnable::run);
            termination.clear();
        }
    }

    /**
     * Register a new action to execute when the current action is terminated
     *
     * @param listener Action to execute
     */
    public void terminated(Runnable listener) {
        if (current == null) {
            listener.run();
            return;
        }

        termination.add(listener);
    }
}

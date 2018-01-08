package fr.quatrevieux.araknemu.game.exploration.action;

import java.util.*;

/**
 * Queue for game actions
 *
 * A new action will be pushed at the end of the queue
 * The action will be started when the last action will be finished, or the queue is empty
 *
 * Example :
 *
 * recv  GA001
 * recv  GA500
 * push  MOVE
 * start MOVE
 * send  GA001
 * push  IO_ACTION
 * ... Wait for move end...
 * GKK001
 * end   MOVE
 * pop   MOVE
 * start IO_ACTION
 * ... Wait for end...
 *
 * The action have 2 different types of ending :
 * - {@link Action#end()}  When an action is successfully ended
 * - {@link Action#cancel(String)} When an error occurs during the action, and should be stopped prematurely
 */
final public class ActionQueue {
    final private Queue<Action> actions = new ArrayDeque<>();
    private byte lastActionId = 0;

    /**
     * Check if the queue is not empty
     */
    public boolean isBusy() {
        return !actions.isEmpty();
    }

    /**
     * Push the action to the queue, and start it if not busy
     */
    public void push(Action action) throws Exception {
        actions.add(action);

        if (actions.size() == 1) {
            run();
        }
    }

    /**
     * End an action which is successfully terminated
     *
     * @param actionId The action to end
     */
    public void end(int actionId) throws Exception {
        if (actions.element().id() != actionId) {
            throw new NoSuchElementException("The action ID do not corresponds");
        }

        actions.remove().end();

        if (isBusy()) {
            run();
        }
    }

    /**
     * Cancel an action in the queue
     *
     * @param actionId Action to cancel
     * @param argument The cancel argument
     *
     * @throws Exception
     */
    public void cancel(int actionId, String argument) throws Exception {
        Action current = actions.element();

        if (current.id() != actionId) {
            throw new NoSuchElementException("The action ID do not corresponds");
        }

        actions.element().cancel(argument);
        actions.clear();
    }

    /**
     * Generate a new action id
     */
    public int generateId() {
        return ++lastActionId;
    }

    /**
     * Run the current action.
     *
     * If an error occurs during the action, it will be removed
     */
    private void run() throws Exception {
        Exception error = null;

        while (isBusy()) {
            try {
                actions.element().start();
                break;
            } catch (Exception e) {
                actions.remove();
                error = e;
            }
        }

        if (error != null) {
            throw error;
        }
    }
}

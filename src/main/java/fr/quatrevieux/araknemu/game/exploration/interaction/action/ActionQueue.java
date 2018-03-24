package fr.quatrevieux.araknemu.game.exploration.interaction.action;

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
 * - {@link BlockingAction#end()}  When an action is successfully ended
 * - {@link BlockingAction#cancel(String)} When an error occurs during the action, and should be stopped prematurely
 */
final public class ActionQueue {
    final private Queue<Action> actions = new ArrayDeque<>();

    private BlockingAction current;
    private byte lastActionId = 0;

    /**
     * Check if the queue is not empty
     */
    public boolean isBusy() {
        return current != null || !actions.isEmpty();
    }

    /**
     * Push the action to the queue, and start it if not busy
     */
    public void push(Action action) throws Exception {
        actions.add(action);

        runNextAction();
    }

    /**
     * End an action which is successfully terminated
     *
     * @param actionId The action to end
     */
    public void end(int actionId) throws Exception {
        if (current == null || current.id() != actionId) {
            throw new NoSuchElementException("The action ID do not corresponds");
        }

        current.end();
        current = null;

        runNextAction();
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
        if (current == null || current.id() != actionId) {
            throw new NoSuchElementException("The action ID do not corresponds");
        }

        try {
            current.cancel(argument);
        } finally {
            current = null;
            actions.clear();
        }
    }

    /**
     * Generate a new action id
     */
    private int generateId() {
        return ++lastActionId;
    }

    /**
     * Run the next pending action
     *
     * If an error occurs during the action, it will be removed
     */
    private void runNextAction() throws Exception {
        Exception error = null;

        while (this.current == null && !actions.isEmpty()) {
            Action action = actions.remove();

            try {
                if (action instanceof BlockingAction) {
                    setCurrent((BlockingAction) action);
                }

                action.start();
            } catch (Exception e) {
                this.current = null;
                error = e;
            }
        }

        if (error != null) {
            throw error;
        }
    }

    private void setCurrent(BlockingAction current) {
        current.setId(generateId());

        this.current = current;
    }
}

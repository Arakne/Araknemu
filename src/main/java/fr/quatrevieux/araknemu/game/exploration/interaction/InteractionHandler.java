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

package fr.quatrevieux.araknemu.game.exploration.interaction;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Handle exploration player interactions
 */
public final class InteractionHandler {
    private final ActionQueue gameActions = new ActionQueue();

    private @Nullable Interaction current;

    /**
     * Check if the player is interacting
     */
    public boolean interacting() {
        return current != null;
    }

    /**
     * Check if the player is busy
     */
    public boolean busy() {
        return interacting() || gameActions.isBusy();
    }

    /**
     * Stop all interactions
     */
    public void stop() {
        if (current != null) {
            current.stop();
            current = null;
        }

        gameActions.stop();
    }

    /**
     * Start the interaction
     */
    public void start(Interaction interaction) {
        if (busy()) {
            throw new IllegalStateException("Player is busy");
        }

        current = interaction.start();
    }

    /**
     * Get the current interaction
     *
     * @param interaction The interaction type
     */
    @SuppressWarnings("unchecked")
    public <T extends Interaction> T get(Class<T> interaction) {
        if (!interaction.isInstance(current)) {
            throw new IllegalArgumentException("Invalid interaction type");
        }

        return (T) current;
    }

    /**
     * Remove the current interaction
     */
    public Interaction remove() {
        if (current == null) {
            throw new IllegalStateException("No interaction found");
        }

        final Interaction interaction = current;

        current = null;

        return interaction;
    }

    /**
     * Push the action to the queue, and start it if not busy
     */
    public void push(Action action) {
        if (interacting() && action instanceof BlockingAction) {
            throw new IllegalStateException("Cannot start blocking action when interacting");
        }

        gameActions.push(action);
    }

    /**
     * End an action which is successfully terminated
     *
     * @param actionId The action to end
     */
    public void end(int actionId) {
        gameActions.end(actionId);
    }

    /**
     * Cancel an action in the queue
     *
     * @param actionId Action to cancel
     * @param argument The cancel argument
     */
    public void cancel(int actionId, String argument) {
        gameActions.cancel(actionId, argument);
    }
}

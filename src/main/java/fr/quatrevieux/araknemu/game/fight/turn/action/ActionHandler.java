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

package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

/**
 * Handle fight action
 */
public final class ActionHandler {
    private final FightTurn turn;
    private final Fight fight;

    private final Collection<Runnable> termination = new ArrayList<>();

    private @Nullable PendingAction pending;

    public ActionHandler(FightTurn turn, Fight fight) {
        this.turn = turn;
        this.fight = fight;
    }

    /**
     * Start a fight action
     *
     * @param action Action to start
     *
     * @return true if the action is successfully started, or false is cannot start the action
     */
    public boolean start(FightAction action) {
        if (pending != null) {
            return false;
        }

        if (!action.validate(turn)) {
            return false;
        }

        final ActionResult result = action.start();

        if (result.success()) {
            pending = new PendingAction(
                action,
                result,
                fight.schedule(this::terminate, action.duration())
            );
        }

        fight.dispatch(new FightActionStarted(action, result));

        if (!result.success()) {
            result.apply(turn);
        }

        return true;
    }

    /**
     * Terminate the current action
     */
    public void terminate() {
        final PendingAction action = pending;

        if (action == null) {
            return;
        }

        action.future.cancel(false);

        // The fight is stopped (caused by a leave) during an action
        if (!fight.active()) {
            return;
        }

        try {
            action.result.apply(turn);
        } finally {
            pending = null;
            fight.dispatch(new FightActionTerminated(action.action));

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
        if (pending == null) {
            listener.run();
            return;
        }

        termination.add(listener);
    }

    private static class PendingAction {
        protected final Action action;
        protected final ActionResult result;
        protected final ScheduledFuture<?> future;

        public PendingAction(Action action, ActionResult result, ScheduledFuture<?> future) {
            this.action = action;
            this.result = result;
            this.future = future;
        }
    }
}

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
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

/**
 * Handle fight action
 */
public final class ActionHandler {
    private final Fight fight;

    private final Collection<Runnable> termination = new ArrayList<>();

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

        final ActionResult result = action.start();

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

        // The fight is stopped (caused by a leave) during an action
        if (!fight.active()) {
            return;
        }

        final Action action = current;

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

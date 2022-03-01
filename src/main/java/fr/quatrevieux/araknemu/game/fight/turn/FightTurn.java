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

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionHandler;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.TurnActionsFactory;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnTerminated;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle a fighter turn
 *
 * @todo turn list on constructor
 */
public final class FightTurn implements Turn {
    private final AtomicBoolean active = new AtomicBoolean(false);

    private final Fighter fighter;
    private final Fight fight;
    private final Duration duration;

    private final ActionHandler actionHandler;
    private final TurnActionsFactory actionFactory;

    private @MonotonicNonNull ScheduledFuture timer;
    private @MonotonicNonNull FighterTurnPoints points;

    @SuppressWarnings({"assignment", "argument"})
    public FightTurn(Fighter fighter, Fight fight, Duration duration) {
        this.fighter = fighter;
        this.fight = fight;
        this.duration = duration;
        this.actionHandler = new ActionHandler(this, fight);
        this.actionFactory = new TurnActionsFactory(this);
    }

    @Override
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

    @Override
    public boolean active() {
        return active.get();
    }

    /**
     * Start the turn
     *
     * @return true if the turn is successfully started, or false when turn needs to be skipped
     */
    @EnsuresNonNull("points")
    @EnsuresNonNullIf(expression = "timer", result = true)
    public boolean start() {
        points = new FighterTurnPoints(fight, fighter);

        if (!fighter.buffs().onStartTurn()) {
            endTurnActions(true);
            return false;
        }

        fighter.play(this);
        active.set(true);
        fight.dispatch(new TurnStarted(this));
        timer = fight.schedule(this::stop, duration);

        return true;
    }

    /**
     * Stop the turn and start the next turn
     */
    @SuppressWarnings("dereference.of.nullable")
    public void stop() {
        if (!active.getAndSet(false)) {
            return;
        }

        timer.cancel(false);

        actionHandler.terminated(() -> {
            fight.dispatch(new TurnStopped(this));
            fighter.stop();

            endTurnActions(false);

            if (fighter.dead()) {
                // Wait for die animation
                // @fixme remove on tests ?
                fight.schedule(() -> fight.turnList().next(), Duration.ofMillis(1500));
            } else {
                fight.turnList().next();
            }
        });
    }

    @Override
    public void perform(Action action) throws FightException {
        if (!active.get()) {
            throw new FightException("Turn is not active");
        }

        if (fighter.dead()) {
            throw new FightException("The fighter is dead");
        }

        if (!actionHandler.start(action)) {
            throw new FightException("Cannot start the action");
        }
    }

    /**
     * Execute the action when the current is terminated
     * If there is no pending action, the action is immediately executed
     */
    public void later(Runnable nextAction) {
        actionHandler.terminated(nextAction);
    }

    /**
     * Terminate the current action
     */
    public void terminate() {
        actionHandler.terminate();
    }

    @Override
    public TurnActionsFactory actions() {
        return actionFactory;
    }

    @Override
    public FighterTurnPoints points() {
        if (points == null) {
            throw new IllegalStateException("Fight turn not yet started");
        }

        return points;
    }

    /**
     * Perform actions on turn ending
     */
    private void endTurnActions(boolean aborted) {
        fighter.buffs().onEndTurn();

        fight.dispatch(new TurnTerminated(this, aborted));
    }
}

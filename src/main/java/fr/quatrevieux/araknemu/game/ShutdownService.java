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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.game.event.ShutdownScheduled;
import fr.quatrevieux.araknemu.game.listener.KickAllOnShutdown;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Handle the scheduled shutdown
 */
public final class ShutdownService implements EventsSubscriber {
    private final Araknemu app;
    private final Dispatcher dispatcher;
    private final GameConfiguration configuration;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> scheduledShutdown;
    private ScheduledFuture<?> shutdownReminder;

    public ShutdownService(Araknemu app, Dispatcher dispatcher, GameConfiguration configuration) {
        this.app = app;
        this.dispatcher = dispatcher;
        this.configuration = configuration;
    }

    /**
     * Shutdown the server immediately
     *
     * @throws IllegalStateException When a shutdown is already scheduled
     */
    public synchronized void now() {
        checkNotScheduled();

        executor.submit(app::shutdown);
    }

    /**
     * Schedule a new shutdown
     *
     * @throws IllegalStateException When a shutdown is already scheduled
     */
    public synchronized void schedule(Duration delay) {
        checkNotScheduled();

        scheduledShutdown = executor.schedule(app::shutdown, delay.toMillis(), TimeUnit.MILLISECONDS);
        remindShutdown(delay);
    }

    /**
     * Cancel the current scheduled shutdown
     * This method will never fail
     *
     * @return false if there is no scheduled shutdown, or cannot cancel.
     */
    public synchronized boolean cancel() {
        if (scheduledShutdown == null) {
            return false;
        }

        if (scheduledShutdown.cancel(false)) {
            scheduledShutdown = null;

            if (shutdownReminder != null) {
                shutdownReminder.cancel(true);
                shutdownReminder = null;
            }

            return true;
        }

        return false;
    }

    /**
     * Get the current delay for the scheduled shutdown
     * An empty optional is returned when no shutdown has been scheduled
     */
    public Optional<Duration> delay() {
        return Optional.ofNullable(scheduledShutdown).map(scheduledFuture -> Duration.ofMillis(scheduledFuture.getDelay(TimeUnit.MILLISECONDS)));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new KickAllOnShutdown(),
            new Listener<GameStopped>() {
                @Override
                public void on(GameStopped event) {
                    executor.shutdown();
                }

                @Override
                public Class<GameStopped> event() {
                    return GameStopped.class;
                }
            },
        };
    }

    /**
     * Throw {@link IllegalStateException} is a shutdown is already scheduled
     */
    private void checkNotScheduled() {
        if (scheduledShutdown != null) {
            throw new IllegalStateException("A shutdown has been scheduled");
        }
    }

    /**
     * Remind the shutdown at the configured minutes
     */
    private void remindShutdown() {
        delay().ifPresent(this::remindShutdown);
    }

    /**
     * Remind the shutdown at the configured minutes
     */
    private void remindShutdown(Duration delay) {
        dispatcher.dispatch(new ShutdownScheduled(delay));

        final long minutes = delay.toMinutes();
        final long[] remindDelays = configuration.shutdownReminderMinutes();

        for (int i = 0; i < remindDelays.length; ++i) {
            // Delay is too low for a remind
            if (minutes <= remindDelays[i]) {
                break;
            }

            // Delay is on an higher interval
            if (i + 1 < remindDelays.length && minutes > remindDelays[i + 1]) {
                continue;
            }

            // Schedule the reminder at the given delay
            executor.schedule((Runnable) this::remindShutdown, minutes - remindDelays[i], TimeUnit.MINUTES);
            break;
        }
    }
}

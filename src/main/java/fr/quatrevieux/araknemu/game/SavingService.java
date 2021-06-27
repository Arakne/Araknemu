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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.event.GameSaved;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.game.event.SavingGame;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle world saving
 */
public final class SavingService implements EventsSubscriber, PreloadableService {
    private final PlayerService playerService;
    private final GameConfiguration configuration;
    private final Dispatcher dispatcher;

    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public SavingService(PlayerService playerService, GameConfiguration configuration, Dispatcher dispatcher) {
        this.playerService = playerService;
        this.configuration = configuration;
        this.dispatcher = dispatcher;
    }

    @Override
    public void preload(Logger logger) {
        if (!configuration.autosaveEnabled()) {
            return;
        }

        scheduleNextSave();
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
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
     * Execute the save
     * Note: this method is non-blocking : the save process is launched in a thread
     *
     * @return true is the save is launched, or false if it's already in progress
     */
    public boolean execute() {
        if (inProgress.getAndSet(true)) {
            return false;
        }

        executor.execute(this::runSave);

        return true;
    }

    /**
     * Schedule the next autosave
     *
     * Note: do not use {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     *       to prevent conflicts on long run. Instead, it will reschedule a save after the previous one
     */
    private void scheduleNextSave() {
        if (executor.isShutdown()) {
            return;
        }

        final Duration interval = configuration.autosaveInterval();

        executor.schedule(() -> {
            if (!inProgress.getAndSet(true)) {
                runSave();
            }

            scheduleNextSave();
        }, interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Run the save process
     * Note: this method will not check the "inProgress" property : it must be checked before
     */
    private void runSave() {
        dispatcher.dispatch(new SavingGame());

        try {
            playerService.online().forEach(GamePlayer::save);
        } finally {
            inProgress.set(false);
            dispatcher.dispatch(new GameSaved());
        }
    }
}

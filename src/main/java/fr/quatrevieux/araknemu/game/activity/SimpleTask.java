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

package fr.quatrevieux.araknemu.game.activity;

import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Base task class
 *
 * The task can be configured using builder methods
 */
public final class SimpleTask implements Task {
    private final Consumer<Logger> action;

    private int maxTries = 0;
    private int retryCount = 0;
    private Duration delay = Duration.ZERO;
    private Duration retryDelay = Duration.ofSeconds(5);
    private String name = "???";

    public SimpleTask(Consumer<Logger> action) {
        this.action = action;
    }

    @Override
    public void execute(Logger logger) {
        action.accept(logger);
    }

    @Override
    public Duration delay() {
        return delay;
    }

    @Override
    public boolean retry(ActivityService service) {
        if (retryCount >= maxTries) {
            return false;
        }

        delay = retryDelay;
        ++retryCount;

        service.execute(this);

        return true;
    }

    /**
     * Set the maximum number of retries when task failed
     * If set to zero, the retry is disabled
     */
    public SimpleTask setMaxTries(int maxTries) {
        this.maxTries = maxTries;

        return this;
    }

    /**
     * Set the execution delay
     *
     * @see Task#delay()
     */
    public SimpleTask setDelay(Duration delay) {
        this.delay = delay;

        return this;
    }

    /**
     * Set the retry delay
     * The retry delay is the waiting delay before re-execute the task
     */
    public SimpleTask setRetryDelay(Duration retryDelay) {
        this.retryDelay = retryDelay;

        return this;
    }

    /**
     * Change the task name
     */
    public SimpleTask setName(String name) {
        this.name = name;

        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}

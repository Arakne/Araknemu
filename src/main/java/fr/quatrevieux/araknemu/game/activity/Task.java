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

/**
 * Activity task
 *
 * @see ActivityService#execute(Task)
 */
public interface Task {
    /**
     * Execute the action
     */
    public void execute(Logger logger);

    /**
     * The waiting delay before execute the task
     */
    public Duration delay();

    /**
     * Try to retry execute the task
     *
     * @return true if can retry, or false if cannot (or reach max tries limit)
     */
    public boolean retry(ActivityService service);

    /**
     * Get the task name
     */
    public String toString();
}

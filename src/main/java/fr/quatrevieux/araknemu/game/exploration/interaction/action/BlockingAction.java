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

package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Blocking action
 * This action will waiting for end or error packet for process other actions
 */
public interface BlockingAction extends Action {
    /**
     * The action must be stopped Unexpectedly
     * Note: This method should not raise any exception : it used for {@link ActionQueue#stop()}
     * After this call, the action is considered as done, and will be destroyed.
     * So it must let the exploration into a valid state
     *
     * @param argument The cancel argument. Null if called by {@link ActionQueue#stop()}
     */
    public void cancel(@Nullable String argument);

    /**
     * End the action normally (i.e. the action is successfully done)
     */
    public void end();

    /**
     * Get the action id
     */
    public int id();

    /**
     * Set the action ID
     *
     * This method is internal
     */
    public void setId(int id);
}

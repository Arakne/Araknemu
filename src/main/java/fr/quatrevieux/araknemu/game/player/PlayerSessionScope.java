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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Store player properties for a game session scope
 */
public interface PlayerSessionScope extends Dispatcher, Sender {
    /**
     * Get the session event dispatcher for register listeners and dispatch event only to current scope
     */
    public ListenerAggregate dispatcher();

    /**
     * Get the properties of the current character session
     */
    public CharacterProperties properties();

    /**
     * Register the scope to the session
     */
    public void register(GameSession session);

    /**
     * Remove the scope from the session
     */
    public void unregister(GameSession session);
}

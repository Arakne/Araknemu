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

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;

/**
 * Leave the fight on disconnect for a spectator
 */
public final class LeaveSpectatorOnDisconnect implements Listener<Disconnected> {
    private final Spectator spectator;

    public LeaveSpectatorOnDisconnect(Spectator spectator) {
        this.spectator = spectator;
    }

    @Override
    public void on(Disconnected event) {
        spectator.leave();
    }

    @Override
    public Class<Disconnected> event() {
        return Disconnected.class;
    }
}

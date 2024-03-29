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

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.event.ShutdownScheduled;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Send to all connected players the scheduled shutdown
 */
public final class SendShutdownScheduled implements Listener<ShutdownScheduled> {
    private final PlayerService service;

    public SendShutdownScheduled(PlayerService service) {
        this.service = service;
    }

    @Override
    public void on(ShutdownScheduled event) {
        // @todo Should be translated : Dofus client do not translate this delay
        service.send(Error.shutdownScheduled(event.delay().toMinutes() + "min"));
    }

    @Override
    public Class<ShutdownScheduled> event() {
        return ShutdownScheduled.class;
    }
}

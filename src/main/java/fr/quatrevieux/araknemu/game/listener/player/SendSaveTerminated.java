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

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.event.GameSaved;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Send to all connected players that the save is terminated
 */
public final class SendSaveTerminated implements Listener<GameSaved> {
    private final PlayerService service;

    public SendSaveTerminated(PlayerService service) {
        this.service = service;
    }

    @Override
    public void on(GameSaved event) {
        service.send(Error.saveTerminated());
    }

    @Override
    public Class<GameSaved> event() {
        return GameSaved.class;
    }
}

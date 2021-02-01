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

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerXpChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Send to client new player xp
 */
public final class SendPlayerXp implements Listener<PlayerXpChanged> {
    private final GamePlayer player;

    public SendPlayerXp(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(PlayerXpChanged event) {
        player.send(new Stats(player.scope().properties()));
    }

    @Override
    public Class<PlayerXpChanged> event() {
        return PlayerXpChanged.class;
    }
}

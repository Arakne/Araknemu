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
import fr.quatrevieux.araknemu.game.player.characteristic.event.LifeChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Send packet after life changed
 */
public final class SendLifeChanged implements Listener<LifeChanged> {
    private final GamePlayer player;

    public SendLifeChanged(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(LifeChanged event) {
        if (event.diff() == 0) {
            return;
        }

        player.send(new Stats(player.scope().properties()));

        if (event.diff() > 0) {
            player.send(Information.heal(event.diff()));
        }
    }

    @Override
    public Class<LifeChanged> event() {
        return LifeChanged.class;
    }
}

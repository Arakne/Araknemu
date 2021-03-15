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
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerLife;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;

/**
 * Restore all life points of the player on level up
 */
public final class RestoreLifePointsOnLevelUp implements Listener<PlayerLevelUp> {
    private final GamePlayer player;

    public RestoreLifePointsOnLevelUp(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(PlayerLevelUp event) {
        final PlayerLife life = player.properties().life();

        life.set(life.max());
    }

    @Override
    public Class<PlayerLevelUp> event() {
        return PlayerLevelUp.class;
    }
}

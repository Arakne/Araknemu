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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;

/**
 * Start the life regeneration when the player joins the game
 */
final public class LifeTimer implements Listener<GameJoined> {
    final private GamePlayer player;
    final private ScheduledExecutorService executor;

    public LifeTimer(GamePlayer player, ScheduledExecutorService executor) {
        this.player = player;
        this.executor = executor;
    }

    @Override
    public void on(GameJoined event) {
        executor.scheduleAtFixedRate(player.lifeRegeneration(), 0, 1000, TimeUnit.MILLISECONDS);
        player.startLifeRegeneration();
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
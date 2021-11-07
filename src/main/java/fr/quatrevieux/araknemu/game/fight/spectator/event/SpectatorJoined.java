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

package fr.quatrevieux.araknemu.game.fight.spectator.event;

import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;

/**
 * A player has joined a fight as spectator
 *
 * @see fr.quatrevieux.araknemu.game.fight.spectator.Spectators#add(Spectator)
 */
public final class SpectatorJoined {
    private final Spectator player;

    public SpectatorJoined(Spectator player) {
        this.player = player;
    }

    /**
     * The spectator
     */
    public Spectator spectator() {
        return player;
    }
}

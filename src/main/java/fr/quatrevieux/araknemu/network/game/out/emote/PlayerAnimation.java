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

package fr.quatrevieux.araknemu.network.game.out.emote;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Send to client the player orientation
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Emotes.as#L116
 *
 * @todo Send for sprite or creature instead of player
 */
final public class PlayerAnimation {
    final private ExplorationPlayer player;
    final private String animation;

    public PlayerAnimation(ExplorationPlayer player, String animation) {
        this.player = player;
        this.animation = animation;
    }

    @Override
    public String toString() {
        return "eUK"+player.id()+"|"+animation;
    }
}

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
 * Copyright (c) 2017-2021 Vincent Quatrevieux, Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.network.game.out.info;

/**
 * This packet tells the client to start the regeneration animation
 * @see https://github.com/Emudofus/Dofus/blob/1b54a30e02f637c912bf14afdf6ea8b7df45ea73/dofus/aks/Infos.as#L326
 */
final public class StartLifeTimer {
    /**
     * The speed of the animation in milliseconds
     */
    final private int time;

    public StartLifeTimer(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ILS" + time;
    }
}

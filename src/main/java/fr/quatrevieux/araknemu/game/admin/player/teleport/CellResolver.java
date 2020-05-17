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

package fr.quatrevieux.araknemu.game.admin.player.teleport;

/**
 * Resolve the target cell
 */
final public class CellResolver implements LocationResolver {
    @Override
    public String name() {
        return "cell";
    }

    @Override
    public void resolve(String argument, Target target) {
        target.setCell(Integer.parseUnsignedInt(argument));
    }

    @Override
    public String help() {
        return "Define the target cell.\nUsage: goto [map target] cell [cellid]";
    }
}

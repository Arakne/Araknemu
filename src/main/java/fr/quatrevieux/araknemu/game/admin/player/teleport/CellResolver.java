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

import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.TeleportationTarget;
import fr.quatrevieux.araknemu.util.ParseUtils;

/**
 * Resolve the target cell
 */
public final class CellResolver implements LocationResolver {
    @Override
    public String name() {
        return "cell";
    }

    @Override
    public TeleportationTarget resolve(String argument, TeleportationTarget target) {
        return target.withCell(ParseUtils.parseNonNegativeInt(argument));
    }

    @Override
    public String help() {
        return "Define the target cell.\nUsage: goto [map target] cell [cellid]";
    }
}

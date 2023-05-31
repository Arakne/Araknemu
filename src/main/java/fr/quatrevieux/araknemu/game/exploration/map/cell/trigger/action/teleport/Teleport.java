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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.map.TeleportationTarget;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Teleport action
 */
public final class Teleport implements CellAction {
    public static final int ACTION_ID = 0;

    private final ExplorationMapService service;
    private final @NonNegative int cell;
    private final Position target;

    public Teleport(ExplorationMapService service, @NonNegative int cell, Position target) {
        this.service = service;
        this.cell = cell;
        this.target = target;
    }

    @Override
    public void perform(ExplorationPlayer player) {
        final TeleportationTarget target = new TeleportationTarget(
            player.map() != null && this.target.map() == player.map().id()
                ? player.map()
                : service.load(this.target.map()),
            this.target.cell()
        );

        target.apply(player);
    }

    @Override
    public @NonNegative int cell() {
        return cell;
    }
}

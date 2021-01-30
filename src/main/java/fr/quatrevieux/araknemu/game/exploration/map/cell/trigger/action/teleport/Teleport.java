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
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;

/**
 * Teleport action
 */
final public class Teleport implements CellAction {
    final static public int ACTION_ID = 0;

    final private ExplorationMapService service;
    final private int cell;
    final private Position target;

    public Teleport(ExplorationMapService service, int cell, Position target) {
        this.service = service;
        this.cell = cell;
        this.target = target;
    }

    @Override
    public void perform(ExplorationPlayer player) {
        final ExplorationMap map = service.load(target.map());

        // teleport on same map
        if (map.equals(player.map())) {
            player.changeCell(target.cell());
            return;
        }

        player.interactions().push(new ChangeMap(player, map, target.cell()));
    }

    @Override
    public int cell() {
        return cell;
    }
}

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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;

/**
 * Cell which trigger an action
 */
final public class TriggerCell implements ExplorationMapCell {
    final private int id;
    final private CellAction action;
    final private ExplorationMap map;

    public TriggerCell(int id, CellAction action, ExplorationMap map) {
        this.id = id;
        this.action = action;
        this.map = map;
    }

    @Override
    public ExplorationMap map() {
        return map;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean walkable() {
        return true;
    }

    @Override
    public boolean free() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TriggerCell that = (TriggerCell) o;

        return id == that.id && map == that.map;
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Perform action when a player stops on the cell after a move
     *
     * @param player The player
     */
    public void onStop(ExplorationPlayer player) {
        action.perform(player);
    }
}

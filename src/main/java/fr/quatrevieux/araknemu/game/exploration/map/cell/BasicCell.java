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

package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Simple cell type
 */
final public class BasicCell implements ExplorationMapCell {
    final private int id;
    final private MapTemplate.Cell template;
    final private ExplorationMap map;

    public BasicCell(int id, MapTemplate.Cell template, ExplorationMap map) {
        this.id = id;
        this.template = template;
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
        return template.active() && template.movement() > 1;
    }

    @Override
    public boolean free() {
        // @todo check movement value
        if (!walkable()) {
            return false;
        }

        for (Creature creature : map.creatures()) {
            if (equals(creature.cell())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BasicCell && equals((BasicCell) obj);
    }

    @Override
    public int hashCode() {
        return id;
    }
}

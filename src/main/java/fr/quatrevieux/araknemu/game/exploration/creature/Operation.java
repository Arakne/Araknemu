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

package fr.quatrevieux.araknemu.game.exploration.creature;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;

/**
 * Operation to apply on a creature
 *
 * @param <R> The result type of the operation.
 *           This value must be returned by the {@link ExplorationCreature#apply(Operation)} method
 *           The type must be Boolean for apply on map using {@link fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell#apply(Operation)}
 */
public interface Operation<R> {
    /**
     * Apply the operation on an exploration player
     */
    public default R onExplorationPlayer(ExplorationPlayer player) {
        return onCreature(player);
    }

    /**
     * Apply the operation on a NPC
     */
    public default R onNpc(GameNpc npc) {
        return onCreature(npc);
    }

    /**
     * Apply the operation on a monster group
     */
    public default R onMonsterGroup(MonsterGroup monsterGroup) {
        return onCreature(monsterGroup);
    }

    /**
     * Generic operation to apply to all creatures
     */
    public default R onCreature(ExplorationCreature creature) {
        return (R) null;
    }
}

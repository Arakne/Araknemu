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
 */
public interface Operation {
    /**
     * Apply the operation a an exploration player
     */
    public default void onExplorationPlayer(ExplorationPlayer player) {}

    /**
     * Apply the operation on a NPC
     */
    public default void onNpc(GameNpc npc) {}

    /**
     * Apply the operation on a monster group
     */
    public default void onMonsterGroup(MonsterGroup monsterGroup) {}
}

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

package fr.quatrevieux.araknemu.game.listener.map.monster;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;

/**
 * Start the PvM fight if the player reach a monster group cell
 */
final public class LaunchMonsterFight implements Listener<PlayerMoveFinished> {
    @Override
    public void on(PlayerMoveFinished event) {
        event.cell().apply(new Operation() {
            private boolean found = false;

            @Override
            public void onMonsterGroup(MonsterGroup monsterGroup) {
                if (!found) {
                    monsterGroup.startFight(event.player());
                    found = true;
                }
            }
        });
    }

    @Override
    public Class<PlayerMoveFinished> event() {
        return PlayerMoveFinished.class;
    }
}

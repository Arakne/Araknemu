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

package fr.quatrevieux.araknemu.game.listener.player.exploration;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;

/**
 * Stop exploration when join fight
 */
final public class LeaveExplorationForFight implements Listener<FightJoined> {
    final private ExplorationPlayer player;

    public LeaveExplorationForFight(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void on(FightJoined event) {
        player.player().stop(player);
    }

    @Override
    public Class<FightJoined> event() {
        return FightJoined.class;
    }
}

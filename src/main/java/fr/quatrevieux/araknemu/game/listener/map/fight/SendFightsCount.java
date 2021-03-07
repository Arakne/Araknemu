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

package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;

/**
 * Update the fights count when fight is stopped
 */
public final class SendFightsCount implements Listener<FightStopped> {
    private final ExplorationMap map;
    private final FightService fightService;

    public SendFightsCount(ExplorationMap map, FightService fightService) {
        this.map = map;
        this.fightService = fightService;
    }

    @Override
    public void on(FightStopped event) {
        map.send(new FightsCount(fightService.fightsByMap(map.id()).size()));
    }

    @Override
    public Class<FightStopped> event() {
        return FightStopped.class;
    }
}

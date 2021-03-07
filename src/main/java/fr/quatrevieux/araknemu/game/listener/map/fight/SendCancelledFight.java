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
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;

/**
 * Send to the map the cancelled fight
 */
public final class SendCancelledFight implements Listener<FightCancelled> {
    private final ExplorationMap map;
    private final FightService fightService;

    public SendCancelledFight(ExplorationMap map, FightService fightService) {
        this.map = map;
        this.fightService = fightService;
    }

    @Override
    public void on(FightCancelled event) {
        map.send(new HideFight(event.fight()));
        map.send(new FightsCount(fightService.fightsByMap(map.id()).size()));
    }

    @Override
    public Class<FightCancelled> event() {
        return FightCancelled.class;
    }
}

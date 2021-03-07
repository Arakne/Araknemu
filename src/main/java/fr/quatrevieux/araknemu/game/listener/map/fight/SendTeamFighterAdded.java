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
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;

import java.util.Collections;

/**
 * Add the fighter to displayed fight team
 */
public final class SendTeamFighterAdded implements Listener<FighterAdded> {
    private final ExplorationMap map;

    public SendTeamFighterAdded(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(FighterAdded event) {
        map.send(new AddTeamFighters(event.fighter().team(), Collections.singleton(event.fighter())));
    }

    @Override
    public Class<FighterAdded> event() {
        return FighterAdded.class;
    }
}

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

package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapReady;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

import java.util.Collection;

/**
 * Load map extra info for join the map
 */
final public class LoadExtraInfo implements PacketHandler<GameSession, AskExtraInfo> {
    final private FightService fightService;

    public LoadExtraInfo(FightService fightService) {
        this.fightService = fightService;
    }

    @Override
    public void handle(GameSession session, AskExtraInfo packet) throws Exception {
        ExplorationMap map = session.exploration().map();

        if (map == null) {
            throw new CloseImmediately("A map should be loaded before get extra info");
        }

        session.send(new AddSprites(map.sprites()));

        Collection<Fight> fights = fightService.fightsByMap(map.id());

        for (Fight fight : fights) {
            if (!(fight.state() instanceof PlacementState)) {
                continue;
            }

            session.send(new ShowFight(fight));

            for (FightTeam team : fight.teams()) {
                map.send(new AddTeamFighters(team));
            }
        }

        map.send(new FightsCount(fights.size()));

        session.send(new MapReady());
    }

    @Override
    public Class<AskExtraInfo> packet() {
        return AskExtraInfo.class;
    }
}

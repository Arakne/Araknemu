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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.ListFightsRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightList;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * List fights on the current map
 */
final public class ListFights implements PacketHandler<GameSession, ListFightsRequest> {
    final private FightService service;

    public ListFights(FightService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, ListFightsRequest packet) {
        session.write(new FightList(service.fightsByMap(session.exploration().map().id())));
    }

    @Override
    public Class<ListFightsRequest> packet() {
        return ListFightsRequest.class;
    }
}

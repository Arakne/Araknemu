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

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.AskFightDetails;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightDetails;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Show details on the fight
 */
public final class ShowFightDetails implements PacketHandler<GameSession, AskFightDetails> {
    private final FightService service;

    public ShowFightDetails(FightService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AskFightDetails packet) {
        final ExplorationMap map = NullnessUtil.castNonNull(session.exploration()).map();

        if (map != null) {
            session.send(new FightDetails(service.getFromMap(map.id(), packet.fightId())));
        }
    }

    @Override
    public Class<AskFightDetails> packet() {
        return AskFightDetails.class;
    }
}

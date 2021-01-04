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

package fr.quatrevieux.araknemu.game.handler.emote;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.emote.SetAnimationRequest;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.emote.PlayerAnimation;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;

/**
 * Change the exploration player orientation for role player
 *
 * @see SetOrientationRequest
 */
final public class ChangeAnimation implements PacketHandler<GameSession, SetAnimationRequest> {
    @Override
    public void handle(GameSession session, SetAnimationRequest packet) throws Exception {
        List<ExplorationCreature> playersOnMap = session.player().exploration().map().creatures()
        .stream().filter(p -> p instanceof ExplorationPlayer)
        .collect(Collectors.toList());

        playersOnMap.forEach( p -> ((ExplorationPlayer)p).send(new PlayerAnimation(session.exploration(), packet.animation())));
    }

    @Override
    public Class<SetAnimationRequest> packet() {
        return SetAnimationRequest.class;
    }
}

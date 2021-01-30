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

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreationError;

/**
 * Create the game session
 */
final public class CreateGame implements PacketHandler<GameSession, CreateGameRequest> {
    final private ExplorationService service;

    public CreateGame(ExplorationService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, CreateGameRequest packet) throws Exception {
        final GamePlayer player = session.player();

        if (packet.type() != CreateGameRequest.Type.EXPLORATION) {
            throw new ErrorPacket(new GameCreationError());
        }

        final ExplorationPlayer exploration = service.create(player);

        player.start(exploration);

        session.send(new GameCreated(CreateGameRequest.Type.EXPLORATION));
        session.dispatch(new StartExploration(exploration));
    }

    @Override
    public Class<CreateGameRequest> packet() {
        return CreateGameRequest.class;
    }
}

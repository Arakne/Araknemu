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

package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequestError;

/**
 * Handle the exchange request
 */
public final class AskExchange implements PacketHandler<GameSession, ExchangeRequest> {
    private final ExchangeFactory factory;

    public AskExchange(ExchangeFactory factory) {
        this.factory = factory;
    }

    @Override
    public void handle(GameSession session, ExchangeRequest packet) throws ErrorPacket {
        final ExplorationPlayer player = session.exploration();

        try {
            if (packet.id().isPresent()) {
                player.interactions().start(factory.create(packet.type(), player, player.map().creature(packet.id().get())));
            }
        } catch (RuntimeException e) {
            throw new ErrorPacket(new ExchangeRequestError(ExchangeRequestError.Error.CANT_EXCHANGE));
        }

        // @todo handle cell
    }

    @Override
    public Class<ExchangeRequest> packet() {
        return ExchangeRequest.class;
    }
}

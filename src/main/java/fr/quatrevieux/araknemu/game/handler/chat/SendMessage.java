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

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.chat.SendMessageError;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Send a message
 */
public final class SendMessage implements PacketHandler<GameSession, Message> {
    private final ChatService service;
    private final GameConfiguration.ChatConfiguration configuration;

    public SendMessage(ChatService service, GameConfiguration.ChatConfiguration configuration) {
        this.service = service;
        this.configuration = configuration;
    }

    @Override
    public void handle(GameSession session, Message packet) throws Exception {
        final GamePlayer player = NullnessUtil.castNonNull(session.player());

        if (!player.restrictions().canChat()) {
            throw new ErrorPacket(Error.cantDoOnCurrentState());
        }

        if (packet.message().isEmpty()) {
            throw new ErrorPacket(new Noop());
        }

        if (!session.get(SpamCheckAttachment.KEY).check(configuration.spamCheckInterval(), configuration.spamCheckMaxCount())) {
            throw new ErrorPacket(ServerMessage.spam());
        }

        try {
            service.send(player, packet);
        } catch (ChatException e) {
            switch (e.error()) {
                case UNAUTHORIZED:
                    throw new ErrorPacket(Error.cantDoOnServer());
                default:
                    throw new ErrorPacket(new SendMessageError(e.error(), packet.target()));
            }
        }
    }

    @Override
    public Class<Message> packet() {
        return Message.class;
    }
}

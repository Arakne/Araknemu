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

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;

/**
 * Save chat channel subscriptions
 */
public final class SaveSubscription implements PacketHandler<GameSession, SubscribeChannels> {
    @Override
    public void handle(GameSession session, SubscribeChannels packet) throws Exception {
        if (packet.isSubscribe()) {
            session.player().subscriptions().addAll(packet.channels());
        } else {
            session.player().subscriptions().removeAll(packet.channels());
        }
    }

    @Override
    public Class<SubscribeChannels> packet() {
        return SubscribeChannels.class;
    }
}

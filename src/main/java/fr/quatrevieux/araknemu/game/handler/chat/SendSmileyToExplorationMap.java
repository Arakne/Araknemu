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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.handler.AbstractExploringPacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.UseSmiley;
import fr.quatrevieux.araknemu.network.game.out.chat.Smiley;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

/**
 * Send the player smiley to map
 */
public final class SendSmileyToExplorationMap extends AbstractExploringPacketHandler<UseSmiley> {
    private final SpamCheckAttachment.Key spamCheckKey;

    public SendSmileyToExplorationMap(SpamCheckAttachment.Key spamCheckKey) {
        this.spamCheckKey = spamCheckKey;
    }

    @Override
    public void handle(GameSession session, ExplorationPlayer exploration, UseSmiley packet) throws Exception {
        final ExplorationMap map = exploration.map();

        if (map == null) {
            return;
        }

        if (!session.get(spamCheckKey).check()) {
            throw new ErrorPacket(ServerMessage.spam());
        }

        map.send(new Smiley(exploration, packet.smiley()));
    }

    @Override
    public Class<UseSmiley> packet() {
        return UseSmiley.class;
    }
}

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

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.core.network.SessionClosed;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handle end of session
 */
final public class StopSession implements PacketHandler<GameSession, SessionClosed> {
    @Override
    public void handle(GameSession session, SessionClosed packet) {
        // Issue #85 : all session scopes forward "Disconnected" event to GamePlayer
        // So, the event should be dispatched only to the first scope to prevent to be dispatched twice
        scopes(session).findFirst().ifPresent(scope -> scope.dispatch(new Disconnected()));
        scopes(session).forEach(scope -> scope.unregister(session));

        if (session.isLogged()) {
            session.account().detach();
        }
    }

    @Override
    public Class<SessionClosed> packet() {
        return SessionClosed.class;
    }

    private Stream<PlayerSessionScope> scopes(GameSession session) {
        return Stream.of(session.exploration(), session.fighter(), session.player()).filter(Objects::nonNull);
    }
}

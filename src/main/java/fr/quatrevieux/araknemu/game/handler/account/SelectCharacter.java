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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.common.session.SessionLog;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.AbstractLoggedPacketHandler;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterSelected;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterSelectionError;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

import java.time.LocalDateTime;

/**
 * Handle character select for entering game
 */
public final class SelectCharacter extends AbstractLoggedPacketHandler<ChoosePlayingCharacter> {
    private final PlayerService service;
    private final GameConfiguration configuration;

    public SelectCharacter(PlayerService service, GameConfiguration configuration) {
        this.service = service;
        this.configuration = configuration;
    }

    @Override
    public void handle(GameSession session, GameAccount account, ChoosePlayingCharacter packet) {
        final GamePlayer player;

        synchronized (session) {
            if (session.player() != null) {
                throw new CloseWithPacket(new CharacterSelectionError());
            }

            try {
                player = service.load(session, account, packet.id());
                player.register(session);
            } catch (EntityNotFoundException e) {
                throw new CloseWithPacket(new CharacterSelectionError());
            }
        }

        session.send(new CharacterSelected(player));
        player.dispatch(new GameJoined());
        session.log().ifPresent(log -> log.setPlayerId(player.id()));

        session.send(Error.welcome());
        session.log().flatMap(SessionLog::last).ifPresent(log -> session.send(Information.lastLogin(LocalDateTime.ofInstant(log.startDate(), configuration.timezone()), log.ipAddress())));
        session.send(Information.currentIpAddress(session.channel().address().getAddress().getHostAddress()));
    }

    @Override
    public Class<ChoosePlayingCharacter> packet() {
        return ChoosePlayingCharacter.class;
    }
}

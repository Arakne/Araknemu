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
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.LoginToken;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenError;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenSuccess;

import java.util.NoSuchElementException;

/**
 * Handle login token to start client session
 */
public final class Login implements PacketHandler<GameSession, LoginToken> {
    private final TokenService tokens;
    private final AccountService service;
    private final SessionLogService logService;

    public Login(TokenService tokens, AccountService service, SessionLogService logService) {
        this.tokens = tokens;
        this.service = service;
        this.logService = logService;
    }

    @Override
    @SuppressWarnings("contracts.precondition") // checker cannot infer non null from generics...
    public void handle(GameSession session, LoginToken packet) {
        if (session.isLogged()) {
            throw new CloseImmediately("Account already attached");
        }

        final GameAccount account;

        try {
            account = service.load(tokens.get(packet.token()));
        } catch (NoSuchElementException | EntityNotFoundException e) {
            throw new CloseWithPacket(new LoginTokenError());
        }

        account.attach(session);

        final SessionLog log = logService.load(session);

        session.setLog(log);
        log.setServerId(account.serverId());

        session.send(new LoginTokenSuccess()); // @todo cipher key
    }

    @Override
    public Class<LoginToken> packet() {
        return LoginToken.class;
    }
}

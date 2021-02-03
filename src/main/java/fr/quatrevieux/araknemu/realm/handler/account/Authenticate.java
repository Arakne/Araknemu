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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.out.Community;
import fr.quatrevieux.araknemu.network.realm.out.GMLevel;
import fr.quatrevieux.araknemu.network.realm.out.HostList;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import fr.quatrevieux.araknemu.network.realm.out.Pseudo;
import fr.quatrevieux.araknemu.network.realm.out.Question;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationRequest;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * Authenticate the client
 */
public final class Authenticate implements PacketHandler<RealmSession, Credentials> {
    private final AuthenticationService service;
    private final HostService hosts;
    private final SessionLogService logService;

    public Authenticate(AuthenticationService service, HostService hosts, SessionLogService logService) {
        this.service = service;
        this.hosts = hosts;
        this.logService = logService;
    }

    @Override
    public void handle(RealmSession session, Credentials packet) {
        service.authenticate(new Request(session, packet));
    }

    @Override
    public Class<Credentials> packet() {
        return Credentials.class;
    }

    private class Request implements AuthenticationRequest {
        private final RealmSession session;
        private final Credentials credentials;

        public Request(RealmSession session, Credentials credentials) {
            this.session = session;
            this.credentials = credentials;
        }

        @Override
        public String username() {
            return credentials.username();
        }

        @Override
        public String password() {
            return session.key().decode(credentials.password());
        }

        @Override
        public void success(AuthenticationAccount account) {
            account.attach(session);
            logService.create(session);

            session.send(new Pseudo(account.pseudo()));
            session.send(new Community(account.community()));
            session.send(new GMLevel(account.isMaster()));
            session.send(new Question(account.question()));
            session.send(new HostList(hosts.all()));
        }

        @Override
        public void invalidCredentials() {
            session.send(new LoginError(LoginError.LOGIN_ERROR));
            session.close();
        }

        @Override
        public void alreadyConnected() {
            session.send(new LoginError(LoginError.ALREADY_LOGGED));
            session.close();
        }

        @Override
        public void isPlaying() {
            session.send(new LoginError(LoginError.ALREADY_LOGGED_GAME_SERVER));
            session.close();
        }

        @Override
        public void banned() {
            session.send(new LoginError(LoginError.BANNED));
            session.close();
        }
    }
}

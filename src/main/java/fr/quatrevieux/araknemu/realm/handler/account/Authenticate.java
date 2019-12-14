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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.out.*;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationRequest;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * Authenticate the client
 */
final public class Authenticate implements PacketHandler<RealmSession, Credentials> {
    private static class Request implements AuthenticationRequest {
        final private HostService hosts;
        final private RealmSession session;
        final private Credentials credentials;

        public Request(HostService hosts, RealmSession session, Credentials credentials) {
            this.hosts = hosts;
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
    }

    final private AuthenticationService service;
    final private HostService hosts;

    public Authenticate(AuthenticationService service, HostService hosts) {
        this.service = service;
        this.hosts   = hosts;
    }

    @Override
    public void handle(RealmSession session, Credentials packet) {
        service.authenticate(
            new Request(hosts, session, packet)
        );
    }

    @Override
    public Class<Credentials> packet() {
        return Credentials.class;
    }
}

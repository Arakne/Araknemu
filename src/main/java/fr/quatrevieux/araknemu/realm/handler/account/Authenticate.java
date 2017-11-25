package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
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

            session.write(new Pseudo(account.pseudo()));
            session.write(new Community(account.community()));
            session.write(new GMLevel(account.isGameMaster()));
            session.write(new Answer(account.answer()));
            session.write(new HostList(hosts.all()));
        }

        @Override
        public void invalidCredentials() {
            session.write(new LoginError(LoginError.LOGIN_ERROR));
            session.close();
        }

        @Override
        public void alreadyConnected() {
            session.write(new LoginError(LoginError.ALREADY_LOGGED));
            session.close();
        }

        @Override
        public void isPlaying() {
            session.write(new LoginError(LoginError.ALREADY_LOGGED_GAME_SERVER));
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

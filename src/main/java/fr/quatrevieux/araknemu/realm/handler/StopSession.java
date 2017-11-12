package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.SessionClosed;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;

/**
 * Handle session closed
 */
final public class StopSession implements PacketHandler<RealmSession, SessionClosed> {
    final private AuthenticationService service;

    public StopSession(AuthenticationService service) {
        this.service = service;
    }

    @Override
    public void handle(RealmSession session, SessionClosed packet) {
        if (session.isLogged()) {
            session.account().detach();
        }
    }

    @Override
    public Class<SessionClosed> packet() {
        return SessionClosed.class;
    }
}

package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.SessionCreated;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.out.HelloConnection;

/**
 * Handle starting session
 */
final public class StartSession implements PacketHandler<RealmSession, SessionCreated> {
    @Override
    public void handle(RealmSession session, SessionCreated packet) {
        session.write(
            new HelloConnection(
                session.key()
            )
        );
    }

    @Override
    public Class<SessionCreated> packet() {
        return SessionCreated.class;
    }
}

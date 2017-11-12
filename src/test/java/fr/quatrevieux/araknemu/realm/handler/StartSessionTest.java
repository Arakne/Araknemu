package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.in.SessionCreated;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartSessionTest extends RealmBaseCase {
    @Test
    void handle() {
        StartSession handler = new StartSession();

        handler.handle(session, new SessionCreated());

        requestStack.assertLast("HC"+session.key().key());
    }
}
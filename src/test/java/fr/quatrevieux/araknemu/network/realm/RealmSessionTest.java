package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.in.PacketParser;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class RealmSessionTest {
    @Test
    void session() {
        IoSession session = new DummySession();

        assertSame(session, new RealmSession(session).session());
    }

    @Test
    void key() {
        RealmSession session = new RealmSession(new DummySession());

        assertEquals(32, session.key().key().length());
        assertSame(session.key(), session.key());

        String key = session.key().key();

        assertEquals(key, session.key().key());
    }

    @Test
    void write() {
        IoSession session = new DummySession();

        new RealmSession(session).write("my message");
    }

    @Test
    void parser() {
        IoSession ioSession = new DummySession();
        RealmSession session = new RealmSession(
            ioSession
        );

        assertFalse(session.hasParser());

        PacketParser parser = Mockito.mock(PacketParser.class);
        session.setParser(parser);

        assertTrue(session.hasParser());
        assertSame(parser, session.parser());
        assertSame(parser, new RealmSession(ioSession).parser());
    }
}

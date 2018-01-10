package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class RealmSessionTest {
    @Test
    void channel() {
        Channel channel = new DummyChannel();

        assertSame(channel, new RealmSession(channel, null).channel());
    }

    @Test
    void key() {
        RealmSession session = new RealmSession(new DummyChannel(), null);

        assertEquals(32, session.key().key().length());
        assertSame(session.key(), session.key());

        String key = session.key().key();

        assertEquals(key, session.key().key());
    }

    @Test
    void write() {
        DummyChannel channel = new DummyChannel();

        new RealmSession(channel, null).write("my message");

        assertEquals("my message", channel.getMessages().lastElement());
    }
}

package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.network.realm.RealmSession;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDispatcherTest {
    public static class MyPacket implements Packet {}
    public static class MyHandler implements PacketHandler<RealmSession, MyPacket> {
        public RealmSession session;
        public MyPacket packet;

        @Override
        public void handle(RealmSession session, MyPacket packet) {
            this.session = session;
            this.packet = packet;
        }

        @Override
        public Class<MyPacket> packet() {
            return MyPacket.class;
        }
    }

    @Test
    void dispatchFound() throws Exception {
        RealmSession session = new RealmSession(new DummySession());
        MyPacket packet = new MyPacket();
        MyHandler handler = new MyHandler();

        DefaultDispatcher<RealmSession> dispatcher = new DefaultDispatcher<>(new MyHandler[]{handler});

        dispatcher.dispatch(session, packet);

        assertSame(session, handler.session);
        assertSame(packet, handler.packet);
    }

    @Test
    void dispatchNotFound() {
        DefaultDispatcher<RealmSession> dispatcher = new DefaultDispatcher<>(new MyHandler[]{});

        assertThrows(HandlerNotFoundException.class, () -> dispatcher.dispatch(new RealmSession(new DummySession()), new MyPacket()));
    }
}
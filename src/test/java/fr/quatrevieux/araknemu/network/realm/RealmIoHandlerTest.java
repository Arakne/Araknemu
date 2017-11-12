package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.in.*;
import fr.quatrevieux.araknemu.network.realm.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.handler.CheckDofusVersion;
import fr.quatrevieux.araknemu.realm.handler.StartSession;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RealmIoHandlerTest extends RealmBaseCase {
    private RealmIoHandler handler;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new RealmIoHandler(
            new DefaultDispatcher<>(new PacketHandler[]{
                new StartSession(),
                new CheckDofusVersion(configuration),
                new PacketHandler() {
                    @Override
                    public void handle(Object session, Packet packet) {

                    }

                    @Override
                    public Class packet() {
                        return Credentials.class;
                    }
                }
            }),
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            )
        );
    }

    @Test
    void sessionOpened() throws Exception {
        handler.sessionOpened(ioSession);

        requestStack.assertLast("HC"+new RealmSession(ioSession).key().key());
    }

    @Test
    void firstMessageReceivedWillCreateParser() throws Exception {
        DummySession session = new DummySession();

        handler.messageReceived(session, "1.29.1");

        assertTrue(new RealmSession(session).hasParser());
    }

    @Test
    void messageReceivedWillUseSameParser() throws Exception {
        DummySession session = new DummySession();

        handler.messageReceived(session, "1.29.1");
        PacketParser parser1 = new RealmSession(session).parser();

        handler.messageReceived(session, "authenticate\n#1hash");
        PacketParser parser2 = new RealmSession(session).parser();

        assertSame(parser1, parser2);
    }

    @Test
    void messageReceivedTwoSessionTwoParser() throws Exception {
        DummySession session1 = new DummySession();
        DummySession session2 = new DummySession();

        handler.messageReceived(session1, "1.29.1");
        PacketParser parser1 = new RealmSession(session1).parser();

        handler.messageReceived(session2, "1.29.1");
        PacketParser parser2 = new RealmSession(session2).parser();

        assertNotSame(parser1, parser2);
    }
}

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.in.*;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.handler.CheckDofusVersion;
import fr.quatrevieux.araknemu.realm.handler.StartSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealmSessionHandlerTest extends RealmBaseCase {
    private RealmSessionHandler handler;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new RealmSessionHandler(
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
    void opened() throws Exception {
        handler.opened(session);

        requestStack.assertLast("HC"+session.key().key());
    }

    @Test
    void create() {
        Channel channel = new DummyChannel();
        RealmSession session = handler.create(channel);

        assertSame(channel, session.channel());
        assertNull(session.account());
        assertFalse(session.isLogged());
        assertNotNull(session.parser());
    }
}

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.handler.EnsureExploring;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExploringLoaderTest extends LoaderTestCase {
    private ExploringLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new ExploringLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertConstainsOnly(EnsureExploring.class, handlers);

        assertHandlePacket(AskExtraInfo.class, handlers);
        assertHandlePacket(GameActionRequest.class, handlers);
        assertHandlePacket(GameActionAcknowledge.class, handlers);
        assertHandlePacket(GameActionCancel.class, handlers);
    }
}

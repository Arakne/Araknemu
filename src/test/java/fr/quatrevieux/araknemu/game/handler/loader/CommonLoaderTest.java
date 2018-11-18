package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.handler.CheckQueuePosition;
import fr.quatrevieux.araknemu.game.handler.SendPong;
import fr.quatrevieux.araknemu.game.handler.StartSession;
import fr.quatrevieux.araknemu.game.handler.StopSession;
import fr.quatrevieux.araknemu.game.handler.account.GenerateName;
import fr.quatrevieux.araknemu.game.handler.account.Login;
import fr.quatrevieux.araknemu.game.handler.account.SendRegionalVersion;
import fr.quatrevieux.araknemu.game.handler.basic.SendDateAndTime;
import fr.quatrevieux.araknemu.network.game.in.QuickPing;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommonLoaderTest extends LoaderTestCase {
    private CommonLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new CommonLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertContainsType(StartSession.class, handlers);
        assertContainsType(StopSession.class, handlers);
        assertContainsType(CheckQueuePosition.class, handlers);
        assertContainsType(Login.class, handlers);
        assertContainsType(SendRegionalVersion.class, handlers);
        assertContainsType(SendDateAndTime.class, handlers);
        assertContainsType(SendPong.class, handlers);
        assertContainsType(GenerateName.class, handlers);
        assertHandlePacket(QuickPing.class, handlers);
    }
}

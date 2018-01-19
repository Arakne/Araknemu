package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.handler.EnsureLogged;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.in.account.DeleteCharacterRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggedLoaderTest extends LoaderTestCase {
    private LoggedLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new LoggedLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertConstainsOnly(EnsureLogged.class, handlers);

        assertHandlePacket(AskCharacterList.class, handlers);
        assertHandlePacket(AddCharacterRequest.class, handlers);
        assertHandlePacket(ChoosePlayingCharacter.class, handlers);
        assertHandlePacket(DeleteCharacterRequest.class, handlers);
    }
}

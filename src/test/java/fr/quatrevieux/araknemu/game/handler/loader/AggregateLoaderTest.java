package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.handler.CheckQueuePosition;
import fr.quatrevieux.araknemu.game.handler.StartSession;
import fr.quatrevieux.araknemu.game.handler.StopSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AggregateLoaderTest extends GameBaseCase {
    @Test
    void load() throws ContainerException {
        Loader l1 = Mockito.mock(Loader.class);
        Loader l2 = Mockito.mock(Loader.class);

        Mockito.when(l1.load(container)).thenReturn(new PacketHandler[] {new StartSession(), new StopSession()});
        Mockito.when(l2.load(container)).thenReturn(new PacketHandler[] {new CheckQueuePosition()});

        AggregateLoader loader = new AggregateLoader(l1, l2);

        PacketHandler[] handlers = loader.load(container);

        assertCount(3, handlers);
        assertContainsType(StartSession.class, handlers);
        assertContainsType(StopSession.class, handlers);
        assertContainsType(CheckQueuePosition.class, handlers);
    }
}

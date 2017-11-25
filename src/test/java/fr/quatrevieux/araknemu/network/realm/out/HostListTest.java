package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.realm.host.GameConnector;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HostListTest {
    @Test
    void testToString() {
        GameHost h1 = new GameHost(Mockito.mock(GameConnector.class), 1, 123, "127.0.0.1");
        h1.setCanLog(true);
        h1.setState(GameHost.State.ONLINE);
        GameHost h2 = new GameHost(Mockito.mock(GameConnector.class), 2, 456, "127.0.0.1");
        h2.setState(GameHost.State.SAVING);

        assertEquals("AH1;1;110;1|2;2;110;0", new HostList(Arrays.asList(h1, h2)).toString());
    }
}

package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PrivateMessageTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        assertEquals(
            "cMKF|1|Bob|Hello World !|my items",
            new PrivateMessage(
                PrivateMessage.TYPE_FROM,
                gamePlayer(),
                "Hello World !",
                "my items"
            ).toString()
        );
    }
}

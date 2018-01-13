package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MessageSentTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        assertEquals(
            "cMK*|1|Bob|Hello World !|my items",
            new MessageSent(
                gamePlayer(),
                ChannelType.MESSAGES,
                "Hello World !",
                "my items"
            ).toString()
        );
    }
}

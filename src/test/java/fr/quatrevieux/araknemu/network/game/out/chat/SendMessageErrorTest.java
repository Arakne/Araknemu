package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageErrorTest {
    @Test
    void generate() {
        assertEquals(
            "cMEftoto",
            new SendMessageError(ChatException.Error.USER_NOT_CONNECTED, "toto").toString()
        );
    }
}
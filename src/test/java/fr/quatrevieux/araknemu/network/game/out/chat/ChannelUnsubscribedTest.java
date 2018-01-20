package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ChannelUnsubscribedTest {
    @Test
    void generate() {
        assertEquals(
            "cC-i*p",
            new ChannelUnsubscribed(
                EnumSet.of(ChannelType.INFO, ChannelType.PRIVATE, ChannelType.MESSAGES)
            ).toString()
        );
    }
}

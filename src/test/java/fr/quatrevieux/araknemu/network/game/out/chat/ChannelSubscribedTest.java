package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ChannelSubscribedTest {
    @Test
    void generate() {
        assertEquals(
            "cC+i*#p$",
            new ChannelSubscribed(
                EnumSet.of(ChannelType.INFO, ChannelType.WISP, ChannelType.MESSAGES)
            ).toString()
        );
    }
}
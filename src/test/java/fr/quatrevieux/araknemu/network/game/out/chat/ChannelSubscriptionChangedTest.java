package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ChannelSubscriptionChangedTest {
    class Impl extends ChannelSubscriptionChanged {
        public Impl(char sign, Collection<ChannelType> channels) {
            super(sign, channels);
        }
    }

    @Test
    void withOneChannel() {
        assertEquals(
            "cC+@",
            new Impl('+', Collections.singleton(ChannelType.ADMIN)).toString()
        );
    }

    @Test
    void withMultipleChannels() {
        assertEquals(
            "cC-ip*",
            new Impl(
                '-',
                Arrays.asList(ChannelType.INFO, ChannelType.PRIVATE, ChannelType.MESSAGES)
            ).toString()
        );
    }
}

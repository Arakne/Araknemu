package fr.quatrevieux.araknemu.network.game.in.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class SubscribeChannelsTest {
    @Test
    void parseSingleChannel() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        SubscribeChannels sub = parser.parse("+i");

        assertTrue(sub.isSubscribe());
        assertEquals(EnumSet.of(ChannelType.INFO), sub.channels());
    }

    @Test
    void parseUnsubscribe() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        SubscribeChannels sub = parser.parse("-i");

        assertFalse(sub.isSubscribe());
        assertEquals(EnumSet.of(ChannelType.INFO), sub.channels());
    }

    @Test
    void parseMultipleChannels() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        SubscribeChannels sub = parser.parse("+#$p");

        assertTrue(sub.isSubscribe());
        assertEquals(EnumSet.of(
            ChannelType.FIGHT_TEAM,
            ChannelType.GROUP,
            ChannelType.PRIVATE
        ), sub.channels());
    }
}

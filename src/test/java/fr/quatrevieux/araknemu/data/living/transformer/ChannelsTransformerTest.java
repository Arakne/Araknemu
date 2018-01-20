package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.network.adapter.Channel;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ChannelsTransformerTest {
    private ChannelsTransformer transformer = new ChannelsTransformer();

    @Test
    void serializeNull() {
        assertEquals("", transformer.serialize(null));
    }

    @Test
    void serializeEmptySet() {
        assertEquals("", transformer.serialize(EnumSet.noneOf(ChannelType.class)));
    }

    @Test
    void serialize() {
        assertEquals("i*:", transformer.serialize(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES, ChannelType.TRADE)));
    }

    @Test
    void unserializeNull() {
        assertEquals(EnumSet.noneOf(ChannelType.class), transformer.unserialize(null));
    }

    @Test
    void unserialize() {
        assertEquals(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES, ChannelType.TRADE), transformer.unserialize("i*:"));
    }
}

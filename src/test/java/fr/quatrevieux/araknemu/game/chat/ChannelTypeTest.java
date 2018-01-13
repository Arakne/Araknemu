package fr.quatrevieux.araknemu.game.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChannelTypeTest {
    @Test
    void byChar() {
        assertSame(ChannelType.ADMIN, ChannelType.byChar('@'));
        assertSame(ChannelType.PRIVATE, ChannelType.byChar('p'));
        assertSame(ChannelType.FIGHT_TEAM, ChannelType.byChar('#'));
    }
}

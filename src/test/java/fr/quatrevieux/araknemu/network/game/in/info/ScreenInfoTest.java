package fr.quatrevieux.araknemu.network.game.in.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScreenInfoTest {
    @Test
    void parse() {
        ScreenInfo.Parser parser = new ScreenInfo.Parser();

        ScreenInfo info = parser.parse("1920;1080;1");

        assertEquals(1920, info.width());
        assertEquals(1080, info.height());
        assertEquals(ScreenInfo.State.FULLSCREEN, info.state());
    }
}

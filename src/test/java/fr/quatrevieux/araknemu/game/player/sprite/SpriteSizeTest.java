package fr.quatrevieux.araknemu.game.player.sprite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpriteSizeTest {
    @Test
    void values() {
        SpriteSize size = new SpriteSize(150, 120);

        assertEquals(150, size.x());
        assertEquals(120, size.y());
        assertEquals("150x120", size.toString());
    }
}
package fr.quatrevieux.araknemu.game.world.creature;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpriteTest {
    @Test
    void toSpriteId() {
        assertEquals(-12304, Sprite.Type.NPC.toSpriteId(123));
        assertEquals(-12306, Sprite.Type.COLLECTOR.toSpriteId(123));
    }
}
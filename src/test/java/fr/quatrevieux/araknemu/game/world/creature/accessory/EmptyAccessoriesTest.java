package fr.quatrevieux.araknemu.game.world.creature.accessory;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EmptyAccessoriesTest extends TestCase {
    @Test
    void string() {
        assertEquals("", new EmptyAccessories().toString());
    }

    @Test
    void get() {
        assertInstanceOf(NullAccessory.class, new EmptyAccessories().get(AccessoryType.HELMET));
    }

    @Test
    void all() {
        assertEquals(new ArrayList<>(), new EmptyAccessories().all());
    }
}

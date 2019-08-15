package fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageKamasTest extends TestCase {
    @Test
    void generate() {
        assertEquals("EsKG15000", new StorageKamas(15000).toString());
    }
}

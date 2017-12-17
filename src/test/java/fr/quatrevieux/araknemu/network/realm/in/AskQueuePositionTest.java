package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskQueuePositionTest {
    @Test
    public void parser() {
        assertTrue(new AskQueuePosition.Parser().parse("Af") instanceof AskQueuePosition);
    }
}

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FightPlacesTransformerTest extends TestCase {
    private FightPlacesTransformer transformer = new FightPlacesTransformer();

    @Test
    void unserializeSuccess() {
        List<Integer>[] places = transformer.unserialize("dKdvdgc3cPc4dhdwdZ|fIftfee1eMeyeNe2fffu");

        assertCount(2, places);

        assertCount(9, places[0]);
        assertCount(10, places[1]);
    }

    @Test
    void unserializeError() {
        assertThrows(RuntimeException.class, () -> transformer.unserialize("  invalid  "));
    }
}

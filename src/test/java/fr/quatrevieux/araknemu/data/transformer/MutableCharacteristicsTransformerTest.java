package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutableCharacteristicsTransformerTest {
    @Test
    void unserializeNull() {
        assertEquals(new DefaultCharacteristics(), new MutableCharacteristicsTransformer().unserialize(null));
    }
}
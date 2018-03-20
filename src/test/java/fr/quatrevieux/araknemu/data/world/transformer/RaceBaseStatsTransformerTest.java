package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.junit.jupiter.api.Test;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.*;

class RaceBaseStatsTransformerTest extends TestCase {
    private RaceBaseStatsTransformer transformer = new RaceBaseStatsTransformer(new ImmutableCharacteristicsTransformer());

    @Test
    void unserialize() {
        SortedMap<Integer, Characteristics> stats = transformer.unserialize("8:6;9:3;h:1;@1|8:7;9:3;h:1;@100");

        assertCount(2, stats.values());

        assertEquals(6, stats.get(1).get(Characteristic.ACTION_POINT));
        assertEquals(3, stats.get(1).get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, stats.get(1).get(Characteristic.MAX_SUMMONED_CREATURES));

        assertEquals(7, stats.get(100).get(Characteristic.ACTION_POINT));
        assertEquals(3, stats.get(100).get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, stats.get(100).get(Characteristic.MAX_SUMMONED_CREATURES));
    }
}
package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonsterListTransformerTest extends TestCase {
    @Test
    void unserialize() {
        MonsterListTransformer transformer = new MonsterListTransformer();

        List<MonsterGroupData.Monster> list = transformer.unserialize("31|34,10|36,3,6");

        assertCount(3, list);

        assertEquals(31, list.get(0).id());
        assertEquals(new Interval(1, Integer.MAX_VALUE), list.get(0).level());
        assertEquals(1, list.get(0).rate());

        assertEquals(34, list.get(1).id());
        assertEquals(new Interval(10, 10), list.get(1).level());
        assertEquals(1, list.get(1).rate());

        assertEquals(36, list.get(2).id());
        assertEquals(new Interval(3, 6), list.get(2).level());
        assertEquals(1, list.get(2).rate());
    }

    @Test
    void unserializeWithRate() {
        MonsterListTransformer transformer = new MonsterListTransformer();

        List<MonsterGroupData.Monster> list = transformer.unserialize("31x3|34,10x2");

        assertEquals(3, list.get(0).rate());
        assertEquals(2, list.get(1).rate());
    }
}
package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.transformer.MonsterListTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterGroupDataRepositoryTest extends GameBaseCase {
    private SqlMonsterGroupDataRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterGroups();

        repository = new SqlMonsterGroupDataRepository(
            app.database().get("game"),
            container.get(MonsterListTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        MonsterGroupData data = repository.get(1);

        assertEquals(1, data.id());
        assertEquals(4, data.maxSize());
        assertEquals(2, data.maxCount());
        assertEquals(Duration.ofMillis(30000), data.respawnTime());
        assertEquals("larves", data.comment());

        assertCount(2, data.monsters());
        assertEquals(31, data.monsters().get(0).id());
        assertEquals(new Interval(1, Integer.MAX_VALUE), data.monsters().get(0).level());
        assertEquals(1, data.monsters().get(0).rate());
        assertEquals(34, data.monsters().get(1).id());
        assertEquals(new Interval(10, 10), data.monsters().get(1).level());
        assertEquals(1, data.monsters().get(1).rate());
        assertEquals(2, data.totalRate());
    }

    @Test
    void getByEntity() {
        MonsterGroupData data = repository.get(new MonsterGroupData(1, null, 0, 0, null, null));

        assertEquals(1, data.id());
        assertEquals(4, data.maxSize());
        assertEquals(2, data.maxCount());
        assertEquals(Duration.ofMillis(30000), data.respawnTime());
        assertEquals("larves", data.comment());

        assertCount(2, data.monsters());
        assertEquals(31, data.monsters().get(0).id());
        assertEquals(new Interval(1, Integer.MAX_VALUE), data.monsters().get(0).level());
        assertEquals(34, data.monsters().get(1).id());
        assertEquals(new Interval(10, 10), data.monsters().get(1).level());
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterGroupData(1, null, 0, 0, null, null)));
        assertFalse(repository.has(new MonsterGroupData(-5, null, 0, 0, null, null)));
    }

    @Test
    void all() {
        assertArrayEquals(
            new int[] {1, 2, 3},
            repository.all().stream().mapToInt(MonsterGroupData::id).toArray()
        );
    }
}

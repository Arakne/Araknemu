package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterRewardRepositoryTest extends GameBaseCase {
    private SqlMonsterRewardRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        repository = new SqlMonsterRewardRepository(app.database().get("game"));
    }

    @Test
    void getNotFound() {
        assertFalse(repository.get(-5).isPresent());
    }

    @Test
    void getById() {
        Optional<MonsterRewardData> reward = repository.get(31);

        assertTrue(reward.isPresent());
        assertEquals(31, reward.get().id());
        assertEquals(new Interval(50, 70), reward.get().kamas());
        assertArrayEquals(new long[] {3, 7, 12, 18, 26}, reward.get().experiences());
    }

    @Test
    void getByEntity() {
        MonsterRewardData reward = repository.get(new MonsterRewardData(31, null, null));

        assertEquals(31, reward.id());
        assertEquals(31, reward.id());
        assertEquals(new Interval(50, 70), reward.kamas());
        assertArrayEquals(new long[] {3, 7, 12, 18, 26}, reward.experiences());
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterRewardData(31, null, null)));
        assertTrue(repository.has(new MonsterRewardData(36, null, null)));
        assertFalse(repository.has(new MonsterRewardData(-1, null, null)));
    }

    @Test
    void all() {
        List<MonsterRewardData> rewards = repository.all();

        assertCount(3, rewards);

        assertEquals(31, rewards.get(0).id());
        assertEquals(34, rewards.get(1).id());
        assertEquals(36, rewards.get(2).id());
    }
}

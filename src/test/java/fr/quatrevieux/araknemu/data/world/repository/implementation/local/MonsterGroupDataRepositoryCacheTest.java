package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonsterGroupDataRepositoryCacheTest extends GameBaseCase {
    private MonsterGroupDataRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterGroups();

        repository = new MonsterGroupDataRepositoryCache(
            container.get(MonsterGroupDataRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(2),
            repository.get(2)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new MonsterGroupData(2, null, 0, 0, null, null)),
            repository.get(2)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new MonsterGroupData(2, null, 0, 0, null, null)));
        assertFalse(repository.has(new MonsterGroupData(-2, null, 0, 0, null, null)));
    }

    @Test
    void hasCached() {
        repository.get(2);
        assertTrue(repository.has(new MonsterGroupData(2, null, 0, 0, null, null)));
    }

    @Test
    void all() {
        List<MonsterGroupData> groups = repository.all();

        assertCount(3, groups);

        for (MonsterGroupData template : groups) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}

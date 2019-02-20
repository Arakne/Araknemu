package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterGroupPositionRepositoryTest  extends GameBaseCase {
    private SqlMonsterGroupPositionRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 1));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, 112), 2));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10540, 112), 2));

        repository = new SqlMonsterGroupPositionRepository(app.database().get("game"));
    }

    @Test
    void get() {
        MonsterGroupPosition position = repository.get(new MonsterGroupPosition(new Position(10340, -1), 0));

        assertEquals(new Position(10340, -1), position.position());
        assertEquals(1, position.groupId());
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterGroupPosition(new Position(10340, -1), 0)));
        assertFalse(repository.has(new MonsterGroupPosition(new Position(404, -1), 0)));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MonsterGroupPosition(new Position(10340, 111), 0)));
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MonsterGroupPosition(new Position(103404, -1), 0)));
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MonsterGroupPosition(new Position(404, 404), 0)));
    }

    @Test
    void byMapNoGroups() {
        assertEquals(Collections.emptyList(), repository.byMap(123));
    }

    @Test
    void byMapSingleGroup() {
        Collection<MonsterGroupPosition> positions = repository.byMap(10540);

        assertCount(1, positions);
        assertEquals(2, positions.stream().findFirst().get().groupId());
    }

    @Test
    void byMapMultipleGroups() {
        Collection<MonsterGroupPosition> positions = repository.byMap(10340);

        assertArrayEquals(
            new int[] {1, 2},
            positions.stream().mapToInt(MonsterGroupPosition::groupId).toArray()
        );
    }

    @Test
    void all() {
        Collection<MonsterGroupPosition> positions = repository.all();

        assertArrayEquals(
            new Object[] {
                new Position(10340, -1),
                new Position(10340, 112),
                new Position(10540, 112),
            },
            positions.stream().map(MonsterGroupPosition::position).toArray()
        );
    }
}

package fr.quatrevieux.araknemu.data.living.repository.player;

import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRepositoryTest extends DatabaseTestCase {
    private PlayerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new PlayerRepository(connection);
        repository.initialize();
    }

    @AfterEach
    void tearDown() {
        repository.destroy();
    }

    @Test
    void addWillSetId() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1);

        Player inserted = repository.add(player);

        assertNotSame(player, inserted);

        assertEquals(1, inserted.id());
        assertEquals(5, inserted.accountId());
        assertEquals(1, inserted.serverId());
        assertEquals("name", inserted.name());
        assertEquals(Race.FECA, inserted.race());
        assertEquals(Sex.MALE, inserted.sex());
        assertEquals(-1, inserted.colors().color1());
        assertEquals(1, inserted.level());
    }

    @Test
    void addAndGet() {
        Player player = repository.add(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1));

        Player get = repository.get(player);

        assertEquals(1, get.id());
        assertEquals(5, get.accountId());
        assertEquals(1, get.serverId());
        assertEquals("name", get.name());
        assertEquals(Race.FECA, get.race());
        assertEquals(Sex.MALE, get.sex());
        assertEquals(-1, get.colors().color1());
        assertEquals(1, get.level());
    }

    @Test
    void has() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1);

        assertFalse(repository.has(player));

        player = repository.add(player);

        assertTrue(repository.has(player));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1)));
    }

    @Test
    void delete() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1);
        player = repository.add(player);

        assertTrue(repository.has(player));

        repository.delete(player);

        assertFalse(repository.has(player));
    }
}

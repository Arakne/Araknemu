package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.transformer.MutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRepositoryTest extends DatabaseTestCase {
    private fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new PlayerRepository(connection, new MutableCharacteristicsTransformer());
        repository.initialize();
    }

    @AfterEach
    void tearDown() {
        repository.destroy();
    }

    @Test
    void addWillSetId() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null);

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
        Player player = repository.add(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));

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
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null);

        assertFalse(repository.has(player));

        player = repository.add(player);

        assertTrue(repository.has(player));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)));
    }

    @Test
    void delete() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null);
        player = repository.add(player);

        assertTrue(repository.has(player));

        repository.delete(player);

        assertFalse(repository.has(player));
    }

    @Test
    void nameExists() {
        assertFalse(repository.nameExists(new Player(-1, 5, 1, "name", null, null, null, 1, null)));
        repository.add(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));
        assertTrue(repository.nameExists(new Player(-1, 5, 1, "name", null, null, null, 1, null)));
    }

    @Test
    void accountCharactersCount() {
        repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));
        repository.add(new Player(-1, 5, 1, "Two", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));
        repository.add(new Player(-1, 5, 2, "Other", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));

        assertEquals(2, repository.accountCharactersCount(new Player(-1, 5, 1, null, null, null, null, 0, null)));
    }

    @Test
    void getForGamePlayerNotFound() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        assertThrows(EntityNotFoundException.class, () -> repository.getForGame(Player.forGame(-1, 123, 2)));
    }

    @Test
    void getForGameBadAccount() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        assertThrows(EntityNotFoundException.class, () -> repository.getForGame(Player.forGame(id, 123, 1)));
    }

    @Test
    void getForGameBadServer() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        assertThrows(EntityNotFoundException.class, () -> repository.getForGame(Player.forGame(id, 5, 2)));
    }

    @Test
    void getForGameSuccess() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        Player player = repository.getForGame(Player.forGame(id, 5, 1));

        assertEquals("One", player.name());
        assertEquals(Race.FECA, player.race());
    }

    @Test
    void insertWithStats() {
        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.ACTION_POINT, 12);
        characteristics.set(Characteristic.STRENGTH, 5);

        Player player = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, characteristics));

        assertEquals(characteristics, repository.get(player).stats());
    }
}

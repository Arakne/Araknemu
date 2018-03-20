package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class PlayerRaceRepositoryCacheTest extends GameBaseCase {
    private PlayerRaceRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();

        repository = new PlayerRaceRepositoryCache(
            container.get(PlayerRaceRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(Race.NO_CLASS));
    }

    @Test
    void get() {
        PlayerRace race = repository.get(Race.CRA);

        assertEquals(Race.CRA, race.race());
        assertEquals("Cra", race.name());
        assertEquals(10300, race.startPosition().map());
        assertEquals(6, race.baseStats().get(1).get(Characteristic.ACTION_POINT));
        assertEquals(7, race.baseStats().get(100).get(Characteristic.ACTION_POINT));
    }

    @Test
    void getWillReturnSameInstance() {
        assertSame(
            repository.get(Race.CRA),
            repository.get(Race.CRA)
        );
    }

    @Test
    void getWithEntity() {
        assertSame(
            repository.get(new PlayerRace(Race.CRA)),
            repository.get(Race.CRA)
        );
    }

    @Test
    void hasLoaded() {
        repository.get(Race.CRA);

        assertTrue(repository.has(new PlayerRace(Race.CRA)));
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new PlayerRace(Race.CRA)));
        assertFalse(repository.has(new PlayerRace(Race.NO_CLASS)));
    }

    @Test
    void load() {
        Collection<PlayerRace> races = repository.load();

        assertCount(12, races);

        for (PlayerRace race : races) {
            assertSame(
                race,
                repository.get(race)
            );
        }
    }
}

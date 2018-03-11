package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.transformer.BoostStatsDataTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRaceRepositoryTest extends GameBaseCase {
    private PlayerRaceRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();

        repository = new PlayerRaceRepository(
            app.database().get("game"),
            new ImmutableCharacteristicsTransformer(),
            new BoostStatsDataTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(Race.NO_CLASS));
    }

    @Test
    void getFound() {
        PlayerRace race = repository.get(Race.FECA);

        assertSame(Race.FECA, race.race());
        assertEquals("Feca", race.name());
        assertTrue(race.baseStats() instanceof DefaultCharacteristics);
        assertEquals(6, race.baseStats().get(Characteristic.ACTION_POINT));
        assertEquals(new Position(10300, 320), race.startPosition());
        assertEquals(2, race.boostStats().get(Characteristic.STRENGTH, 15).cost());
        assertEquals(100, race.startDiscernment());
        assertEquals(1000, race.startPods());
        assertEquals(50, race.startLife());
        assertEquals(5, race.perLevelLife());
        assertArrayEquals(new int[] {3, 6, 17}, race.spells());
    }

    @Test
    void getWithPlayerRaceEntity() {
        assertEquals(
            Race.FECA,
            repository.get(
                new PlayerRace(Race.FECA)
            ).race()
        );
    }

    @Test
    void has() {
        assertTrue(repository.has(new PlayerRace(Race.FECA)));
        assertFalse(repository.has(new PlayerRace(Race.NO_CLASS)));
    }

    @Test
    void load() {
        Collection<PlayerRace> loaded = repository.load();

        assertCount(12, loaded);
    }
}

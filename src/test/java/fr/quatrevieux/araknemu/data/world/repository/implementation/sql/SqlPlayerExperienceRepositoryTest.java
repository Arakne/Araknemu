package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlPlayerExperienceRepositoryTest extends GameBaseCase {
    private SqlPlayerExperienceRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushExperience();

        repository = new SqlPlayerExperienceRepository(app.database().get("game"));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new PlayerExperience(741, 0)));
    }

    @Test
    void getFound() {
        PlayerExperience xp = repository.get(new PlayerExperience(12, 0));

        assertEquals(12, xp.level());
        assertEquals(32600, xp.experience());
    }

    @Test
    void has() {
        assertTrue(repository.has(new PlayerExperience(150, 0)));
        assertFalse(repository.has(new PlayerExperience(256, 0)));
    }

    @Test
    void all() {
        List<PlayerExperience> levels = repository.all();

        assertCount(200, levels);

        assertEquals(1, levels.get(0).level());
        assertEquals(200, levels.get(199).level());
    }
}

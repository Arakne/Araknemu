package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class NpcTemplateRepositoryCacheTest extends GameBaseCase {
    private NpcTemplateRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcTemplates();

        repository = new NpcTemplateRepositoryCache(
            container.get(NpcTemplateRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(848),
            repository.get(848)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0)),
            repository.get(848)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0)));
        assertFalse(repository.has(new NpcTemplate(-1, 0, 0, 0, null, null, null, 0, 0)));
    }

    @Test
    void hasCached() {
        repository.get(848);
        assertTrue(repository.has(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0)));
    }

    @Test
    void all() {
        Collection<NpcTemplate> templates = repository.all();

        assertCount(3, templates);

        for (NpcTemplate template : templates) {
            assertSame(template, repository.get(template));
        }
    }
}

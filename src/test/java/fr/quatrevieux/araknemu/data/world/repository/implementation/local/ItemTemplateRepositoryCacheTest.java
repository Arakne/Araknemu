package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ItemTemplateRepositoryCacheTest extends GameBaseCase {
    private ItemTemplateRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        repository = new ItemTemplateRepositoryCache(
            container.get(ItemTemplateRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(40),
            repository.get(40)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)),
            repository.get(40)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)));
        assertFalse(repository.has(new ItemTemplate(-5, 0, null, 0, null, 0, null, 0, null, 0)));
    }

    @Test
    void hasCached() {
        repository.get(40);
        assertTrue(repository.has(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)));
    }

    @Test
    void load() {
        Collection<ItemTemplate> templates = repository.load();

        assertCount(18, templates);

        for (ItemTemplate template : templates) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}

package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ItemSetRepositoryCacheTest extends GameBaseCase {
    private ItemSetRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new ItemSetRepositoryCache(
            container.get(ItemSetRepository.class)
        );

        dataSet.pushItemSets();
    }


    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(7),
            repository.get(7)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new ItemSet(7, null, null)),
            repository.get(7)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new ItemSet(7, null, null)));
        assertFalse(repository.has(new ItemSet(-5, null, null)));
    }

    @Test
    void hasCached() {
        repository.get(7);
        assertTrue(repository.has(new ItemSet(7, null, null)));
    }

    @Test
    void load() {
        Collection<ItemSet> templates = repository.load();

        assertCount(2, templates);

        for (ItemSet template : templates) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}
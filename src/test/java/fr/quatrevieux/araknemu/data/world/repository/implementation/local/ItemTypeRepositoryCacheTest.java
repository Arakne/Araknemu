package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ItemTypeRepositoryCacheTest extends GameBaseCase {
    private ItemTypeRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new ItemTypeRepositoryCache(
            container.get(ItemTypeRepository.class)
        );

        dataSet.pushItemTypes();
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
            repository.get(new ItemType(7, null, null, null)),
            repository.get(7)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new ItemType(7, null, null, null)));
        assertFalse(repository.has(new ItemType(-5, null, null, null)));
    }

    @Test
    void hasCached() {
        repository.get(7);
        assertTrue(repository.has(new ItemType(7, null, null, null)));
    }

    @Test
    void load() {
        Collection<ItemType> templates = repository.load();

        assertCount(114, templates);

        for (ItemType template : templates) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}
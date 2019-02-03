package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.transformer.ItemSetBonusTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlItemSetRepositoryTest extends GameBaseCase {
    private SqlItemSetRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlItemSetRepository(
            app.database().get("game"),
            new ItemSetBonusTransformer()
        );

        dataSet.pushItemSets();
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        ItemSet itemSet = repository.get(1);

        assertEquals(1, itemSet.id());
        assertEquals("Panoplie du Bouftou", itemSet.name());
        assertCount(6, itemSet.bonus());
    }

    @Test
    void getByEntity() {
        ItemSet itemSet = repository.get(new ItemSet(1, null, null));

        assertEquals(1, itemSet.id());
        assertEquals("Panoplie du Bouftou", itemSet.name());
        assertCount(6, itemSet.bonus());
    }

    @Test
    void has() {
        assertTrue(repository.has(new ItemSet(1, null, null)));
        assertFalse(repository.has(new ItemSet(-5, null, null)));
    }

    @Test
    void load() {
        Collection<ItemSet> sets = repository.load();

        assertCount(3, sets);
    }
}

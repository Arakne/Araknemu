package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.transformer.EffectAreaTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.SuperType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ItemTypeRepositoryTest extends GameBaseCase {
    private ItemTypeRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new ItemTypeRepository(app.database().get("game"), new EffectAreaTransformer());

        dataSet.pushItemTypes();
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        ItemType type = repository.get(1);

        assertEquals(1, type.id());
        assertEquals("Amulette", type.name());
        assertSame(SuperType.AMULET, type.superType());
        assertNull(type.effectArea());
    }

    @Test
    void getWithEffectArea() {
        ItemType type = repository.get(7);

        assertEquals(7, type.id());
        assertEquals("Marteau", type.name());
        assertSame(SuperType.WEAPON, type.superType());
        assertEquals(new EffectArea(EffectArea.Type.CROSS, 1), type.effectArea());
    }

    @Test
    void getByEntity() {
        ItemType type = repository.get(new ItemType(1, null, null, null));

        assertEquals(1, type.id());
        assertEquals("Amulette", type.name());
        assertSame(SuperType.AMULET, type.superType());
        assertNull(type.effectArea());
    }

    @Test
    void has() {
        assertTrue(repository.has(new ItemType(1, null, null, null)));
        assertFalse(repository.has(new ItemType(-5, null, null, null)));
    }

    @Test
    void load() {
        Collection<ItemType> types = repository.load();

        assertCount(114, types);
    }
}
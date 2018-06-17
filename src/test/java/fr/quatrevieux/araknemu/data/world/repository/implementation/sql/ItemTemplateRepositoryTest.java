package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ItemTemplateRepositoryTest extends GameBaseCase {
    private ItemTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplate(new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100));
        dataSet.pushItemTemplate(new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200));
        dataSet.pushItemTemplate(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10));

        repository = new ItemTemplateRepository(
            app.database().get("game"),
            container.get(ItemEffectsTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        ItemTemplate item = repository.get(39);

        assertEquals(39, item.id());
        assertEquals(1, item.type());
        assertEquals("Petite Amulette du Hibou", item.name());
        assertCount(1, item.effects());
        assertEquals(Effect.ADD_INTELLIGENCE, item.effects().get(0).effect());
        assertEquals(2, item.effects().get(0).min());
        assertEquals(4, item.weight());
        assertEquals(100, item.price());
    }

    @Test
    void getByTemplate() {
        ItemTemplate item = repository.get(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0));

        assertEquals(40, item.id());
        assertEquals(6, item.type());
        assertEquals("Petite Epée de Boisaille", item.name());
        assertCount(1, item.effects());
        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, item.effects().get(0).effect());
        assertEquals(1, item.effects().get(0).min());
        assertEquals(7, item.effects().get(0).max());
        assertEquals(20, item.weight());
        assertEquals(200, item.price());
        assertEquals("4;1;1;50;30;5;0", item.weaponInfo());
        assertEquals("CS>4", item.condition());
    }

    @Test
    void has() {
        assertTrue(repository.has(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)));
        assertFalse(repository.has(new ItemTemplate(-5, 0, null, 0, null, 0, null, 0, null, 0)));
    }

    @Test
    void load() {
        assertCount(3, repository.load());
        assertContains(new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), repository.load());
        assertContains(new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200), repository.load());
        assertContains(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), repository.load());
    }
}

package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerItemSetTest extends GameBaseCase {
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemSets()
            .pushItemTemplates()
        ;

        service = container.get(ItemService.class);
    }

    @Test
    void empty() {
        PlayerItemSet set = new PlayerItemSet(
            service.itemSet(1)
        );

        assertEquals(1, set.id());
        assertTrue(set.isEmpty());
        assertCount(0, set.bonus().characteristics());
        assertCount(0, set.bonus().specials());
        assertCount(0, set.bonus().effects());
        assertCount(0, set.items());
    }

    @Test
    void add() throws ContainerException {
        PlayerItemSet set = new PlayerItemSet(service.itemSet(1));

        ItemTemplate template = container.get(ItemTemplateRepository.class).get(2425);
        set.add(template);

        assertEquals(1, set.id());
        assertFalse(set.isEmpty());
        assertCount(0, set.bonus().characteristics());
        assertCount(0, set.bonus().specials());
        assertCount(0, set.bonus().effects());
        assertCount(1, set.items());
        assertContainsAll(set.items(), template);

        ItemTemplate template1 = container.get(ItemTemplateRepository.class).get(2411);
        set.add(template1);
        assertCount(2, set.items());
        assertContainsAll(set.items(), template, template1);

        assertCount(2, set.bonus().characteristics());
        assertCount(2, set.bonus().effects());

        assertEquals(Effect.ADD_STRENGTH, set.bonus().characteristics().get(0).effect());
        assertEquals(5, set.bonus().characteristics().get(0).value());
        assertEquals(Effect.ADD_INTELLIGENCE, set.bonus().characteristics().get(1).effect());
        assertEquals(5, set.bonus().characteristics().get(1).value());
    }
}

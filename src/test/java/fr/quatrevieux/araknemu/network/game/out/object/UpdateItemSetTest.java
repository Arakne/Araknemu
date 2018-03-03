package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UpdateItemSetTest extends GameBaseCase {
    private ItemService service;
    private ItemTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemSets()
            .pushItemTemplates()
        ;
        service = container.get(ItemService.class);
        repository = container.get(ItemTemplateRepository.class);
    }

    @Test
    void generateWithEmptySet() {
        assertEquals(
            "OS-1",
            new UpdateItemSet(
                new PlayerItemSet(
                    service.itemSet(1)
                )
            ).toString()
        );
    }

    @Test
    void generateWithItems() {
        assertEquals(
            "OS+1|2425;2411|76#5#0#0#,7e#5#0#0#",
            new UpdateItemSet(
                new PlayerItemSet(
                    service.itemSet(1),
                    new HashSet<>(Arrays.asList(
                        repository.get(2425),
                        repository.get(2411)
                    ))
                )
            ).toString()
        );
    }
}

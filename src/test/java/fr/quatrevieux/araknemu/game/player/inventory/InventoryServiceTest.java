package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest extends GameBaseCase {
    private InventoryService service;
    private PlayerItemRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);
        dataSet.pushItemTemplates();

        service = new InventoryService(
            repository = container.get(PlayerItemRepository.class),
            container.get(ItemService.class),
            new DefaultListenerAggregate()
        );
    }

    @Test
    void loadEmptyInventory() {
        PlayerInventory inventory = service.load(new Player(5));

        assertFalse(inventory.iterator().hasNext());
    }

    @Test
    void loadInventory() throws ItemNotFoundException {
        Player player = new Player(5);

        repository.add(new PlayerItem(5, 3, 39, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")), 1, 0));
        repository.add(new PlayerItem(5, 8, 40, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 1, 1));
        repository.add(new PlayerItem(5, 32, 284, new ArrayList<>(), 10, -1));

        PlayerInventory inventory = service.load(player);

        assertEquals(39, inventory.get(3).templateId());
        assertEquals(40, inventory.get(8).templateId());
        assertEquals(284, inventory.get(32).templateId());
    }
}

package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NpcStoreServiceTest extends GameBaseCase {
    private NpcStoreService service;
    private NpcTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushNpcs()
            .pushNpcWithStore()
        ;

        repository = container.get(NpcTemplateRepository.class);
        service = new NpcStoreService(
            container.get(ItemService.class),
            container.get(ItemTemplateRepository.class),
            configuration.economy()
        );
    }

    @Test
    void loadWithoutStore() {
        assertFalse(service.load(repository.get(848)).isPresent());
    }

    @Test
    void loadWithStore() {
        NpcStore store = service.load(repository.get(10001)).get();

        assertArrayEquals(new int[] {39, 2425}, store.available().stream().mapToInt(ItemTemplate::id).toArray());
        assertSame(store, service.load(repository.get(10001)).get());
    }
}

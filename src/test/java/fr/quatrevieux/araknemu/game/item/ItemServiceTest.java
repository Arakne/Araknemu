package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.type.Wearable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest extends GameBaseCase {
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        service = new ItemService(
            container.get(ItemTemplateRepository.class),
            container.get(ItemFactory.class)
        );
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading items...");
        Mockito.verify(logger).info("Successfully load {} items", 4);
    }

    @Test
    void create() {
        Item item = service.create(39);

        assertEquals(39, item.template().id());
        assertInstanceOf(Wearable.class, item);
        assertEquals(Type.AMULETTE, item.template().type());
        assertCount(1, item.effects());

        Wearable wearable = (Wearable) item;

        assertEquals(Characteristic.INTELLIGENCE, wearable.characteristics().get(0).characteristic());
        assertEquals(2, wearable.characteristics().get(0).boost());
    }

    @Test
    void createRandomStats() {
        Item item1 = service.create(2425);
        Item item2 = service.create(2425);

        assertNotEquals(item1, item2);
    }

    @Test
    void createMaxStats() {
        Item item1 = service.create(2425, true);
        Item item2 = service.create(2425, true);

        assertEquals(item1, item2);

        Wearable wearable = (Wearable) item1;

        assertEquals(Characteristic.INTELLIGENCE, wearable.characteristics().get(0).characteristic());
        assertEquals(10, wearable.characteristics().get(0).boost());
        assertEquals(Characteristic.STRENGTH, wearable.characteristics().get(1).characteristic());
        assertEquals(10, wearable.characteristics().get(1).boost());
    }

    @Test
    void retrieve() {
        Item item = service.create(2425);

        assertEquals(
            item,
            service.retrieve(
                2425,
                item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList())
            )
        );
    }
}

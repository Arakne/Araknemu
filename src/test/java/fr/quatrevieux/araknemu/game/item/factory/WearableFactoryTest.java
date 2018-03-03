package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WearableFactoryTest extends GameBaseCase {
    private WearableFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemSets();

        factory = new WearableFactory(
            new EffectToCharacteristicMapping(),
            new EffectToSpecialMapping()
        );
    }

    @Test
    void createSimple() {
        Item item = factory.create(new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), null, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(1, item.effects());

        Wearable wearable = (Wearable) item;

        assertCount(1, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(2, wearable.characteristics().get(0).value());
    }

    @Test
    void createRandomStats() throws ContainerException {
        ItemTemplate template = new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550);
        GameItemSet set = container.get(ItemService.class).itemSet(1);

        Item item = factory.create(template, set, false);

        assertInstanceOf(Wearable.class, item);
        assertCount(2, item.effects());
        assertSame(template, item.template());
        assertSame(set, item.set().get());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertBetween(1, 10, wearable.characteristics().get(0).value());
        assertEquals(Effect.ADD_STRENGTH, wearable.characteristics().get(1).effect());
        assertBetween(1, 10, wearable.characteristics().get(1).value());
    }

    @Test
    void createMaxStats() throws ContainerException {
        ItemTemplate template = new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3,
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"),
                new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0"),
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 0, 200, 0, "")
            ), 10, "", 1, "", 550
        );

        GameItemSet set = container.get(ItemService.class).itemSet(1);

        Item item = factory.create(template, set, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(3, item.effects());
        assertSame(template, item.template());
        assertSame(set, item.set().get());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(10, wearable.characteristics().get(0).value());
        assertEquals(Effect.ADD_STRENGTH, wearable.characteristics().get(1).effect());
        assertEquals(10, wearable.characteristics().get(1).value());

        assertCount(1, item.specials());
        assertEquals(200, item.specials().get(0).arguments()[0]);
    }

    @Test
    void createSpecial() {
        ItemTemplate template = new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3, Arrays.asList(
            new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"),
            new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0"),
            new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "")
        ), 10, "", 1, "", 550);

        Item item = factory.create(template, null, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(3, item.effects());
        assertSame(template, item.template());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertCount(1, wearable.specials());
        assertEquals(Effect.NULL1, wearable.specials().get(0).effect());
    }

    @Test
    void retrieve() throws ContainerException {
        GameItemSet set = container.get(ItemService.class).itemSet(1);

        Item item = factory.retrieve(
            new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
            set,
            Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 20, 0, 0, ""))
        );

        assertInstanceOf(Wearable.class, item);
        assertCount(1, item.effects());

        Wearable wearable = (Wearable) item;

        assertCount(1, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(20, wearable.characteristics().get(0).value());
        assertSame(set, item.set().get());
    }
}

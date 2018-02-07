package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.world.item.type.Wearable;
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

        factory = new WearableFactory(
            new EffectToCharacteristicMapping(),
            new EffectToSpecialMapping()
        );
    }

    @Test
    void createSimple() {
        Item item = factory.create(new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), true);

        assertInstanceOf(Wearable.class, item);
        assertCount(1, item.effects());

        Wearable wearable = (Wearable) item;

        assertCount(1, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(2, wearable.characteristics().get(0).value());
    }

    @Test
    void createRandomStats() {
        ItemTemplate template = new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550);

        Item item = factory.create(template, false);

        assertInstanceOf(Wearable.class, item);
        assertCount(2, item.effects());
        assertSame(template, item.template());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertBetween(1, 10, wearable.characteristics().get(0).value());
        assertEquals(Effect.ADD_STRENGTH, wearable.characteristics().get(1).effect());
        assertBetween(1, 10, wearable.characteristics().get(1).value());
    }

    @Test
    void createMaxStats() {
        ItemTemplate template = new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550);

        Item item = factory.create(template, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(2, item.effects());
        assertSame(template, item.template());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(10, wearable.characteristics().get(0).value());
        assertEquals(Effect.ADD_STRENGTH, wearable.characteristics().get(1).effect());
        assertEquals(10, wearable.characteristics().get(1).value());
    }

    @Test
    void createSpecial() {
        ItemTemplate template = new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3, Arrays.asList(
            new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"),
            new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0"),
            new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "")
        ), 10, "", 1, "", 550);

        Item item = factory.create(template, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(3, item.effects());
        assertSame(template, item.template());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertCount(1, wearable.specials());
        assertEquals(Effect.NULL1, wearable.specials().get(0).effect());
    }
}

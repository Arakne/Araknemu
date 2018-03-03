package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ResourceFactoryTest extends GameBaseCase {
    private ResourceFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ResourceFactory(
            new EffectToSpecialMapping()
        );
    }

    @Test
    void createSimple() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Item item = factory.create(template, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
    }

    @Test
    void createWillFilterNonSpecialEffects() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_FIRE, 1, 2, 0, "")), 1, "", 0, "", 10);
        Item item = factory.create(template, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
    }

    @Test
    void createWithSpecialEffect() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "test")), 1, "", 0, "", 10);
        Item item = factory.create(template, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertCount(1, item.effects());

        assertEquals(Effect.NULL1, item.effects().get(0).effect());
        assertEquals("test", item.specials().get(0).text());
    }

    @Test
    void retrieveWithSpecialEffect() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Item item = factory.retrieve(template, null, Arrays.asList(new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "test")));

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertCount(1, item.effects());

        assertEquals(Effect.NULL1, item.effects().get(0).effect());
        assertEquals("test", item.specials().get(0).text());
    }
}

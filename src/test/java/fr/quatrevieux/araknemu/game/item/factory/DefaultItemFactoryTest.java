package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultItemFactoryTest extends GameBaseCase {
    @Test
    void typeNotFound() {
        DefaultItemFactory factory = new DefaultItemFactory();

        assertThrows(NoSuchElementException.class, () -> factory.create(new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), null, false), "Invalid type AMULETTE");
        assertThrows(NoSuchElementException.class, () -> factory.retrieve(new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), null, new ArrayList<>()), "Invalid type AMULETTE");
    }

    @Test
    void createSuccess() throws ContainerException {
        DefaultItemFactory factory = new DefaultItemFactory(
            new ResourceFactory(container.get(EffectMappers.class))
        );

        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Item item = factory.create(template, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
    }

    @Test
    void retrieveSuccess() throws ContainerException {
        DefaultItemFactory factory = new DefaultItemFactory(
            new ResourceFactory(container.get(EffectMappers.class))
        );

        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Item item = factory.retrieve(template, null, new ArrayList<>());

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
    }
}

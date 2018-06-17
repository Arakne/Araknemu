package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.NullEffectHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest extends GameBaseCase {
    @Test
    void getters() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Resource resource = new Resource(
            template,
            new ItemType(48, "Poudre", SuperType.RESOURCE, null),
            Arrays.asList(
                new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, "")
            )
        );

        assertSame(template, resource.template());
        assertIterableEquals(
            Arrays.asList(
                new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, "")
            ),
            resource.specials()
        );

        assertFalse(resource.set().isPresent());
    }

    @Test
    void specials() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Resource resource = new Resource(
            template,
            new ItemType(48, "Poudre", SuperType.RESOURCE, null),
            Arrays.asList(
                new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, "")
            )
        );

        assertIterableEquals(
            Arrays.asList(
                new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, "")
            ),
            resource.effects()
        );
    }

    @Test
    void equalsBadClass() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Resource resource = new Resource(
            template,
            new ItemType(48, "Poudre", SuperType.RESOURCE, null),
            Arrays.asList(
                new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, "")
            )
        );

        assertNotEquals(resource, new Object());
    }

    @Test
    void equalsBadTemplate() {
        assertNotEquals(
            new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>()),
            new Resource(new ItemTemplate(285, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>())
        );
    }

    @Test
    void equalsBadStats() {
        assertNotEquals(
            new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), Arrays.asList(new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, ""))),
            new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>())
        );
    }

    @Test
    void equalsOk() {
        assertEquals(
            new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>()),
            new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>())
        );
    }
}

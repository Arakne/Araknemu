package fr.quatrevieux.araknemu.game.world.item.type;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest extends GameBaseCase {
    @Test
    void getters() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Resource resource = new Resource(
            template,
            Arrays.asList(
                new SpecialEffect(Effect.NULL1, new int[] {0, 0, 0}, "")
            )
        );

        assertSame(template, resource.template());
        assertIterableEquals(
            Arrays.asList(
                new SpecialEffect(Effect.NULL1, new int[] {0, 0, 0}, "")
            ),
            resource.specials()
        );
    }

    @Test
    void specials() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Resource resource = new Resource(
            template,
            Arrays.asList(
                new SpecialEffect(Effect.NULL1, new int[] {0, 0, 0}, "")
            )
        );

        assertIterableEquals(
            Arrays.asList(
                new SpecialEffect(Effect.NULL1, new int[] {0, 0, 0}, "")
            ),
            resource.effects()
        );
    }

    @Test
    void equalsBadClass() {
        ItemTemplate template = new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        Resource resource = new Resource(
            template,
            Arrays.asList(
                new SpecialEffect(Effect.NULL1, new int[] {0, 0, 0}, "")
            )
        );

        assertNotEquals(resource, new Object());
    }

    @Test
    void equalsBadTemplate() {
        assertNotEquals(
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()),
            new Resource(new ItemTemplate(285, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>())
        );
    }

    @Test
    void equalsBadStats() {
        assertNotEquals(
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), Arrays.asList(new SpecialEffect(Effect.NULL1, new int[] {0, 0, 0}, ""))),
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>())
        );
    }

    @Test
    void equalsOk() {
        assertEquals(
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()),
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>())
        );
    }
}

package fr.quatrevieux.araknemu.game.world.item.effect;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpecialEffectTest extends TestCase {
    @Test
    void getters() {
        SpecialEffect effect = new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a");

        assertEquals(Effect.NULL1, effect.effect());
        assertArrayEquals(new int[] {1, 2, 3}, effect.arguments());
        assertEquals("a", effect.text());
    }

    @Test
    void equalsSameInstance() {
        SpecialEffect effect = new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a");

        assertEquals(effect, effect);
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a")
        );
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a"), new Object());
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(Effect.NULL2, new int[] {1, 2, 3}, "a")
        );
    }

    @Test
    void equalsBadArgument() {
        assertNotEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(Effect.NULL1, new int[] {0, 2, 3}, "a")
        );
    }

    @Test
    void equalsBadText() {
        assertNotEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "b")
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a").hashCode(),
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a").hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a").hashCode(),
            new SpecialEffect(Effect.NULL2, new int[] {1, 2, 3}, "a").hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "SpecialEffect{NULL2:[1, 2, 3], 'a'}",
            new SpecialEffect(Effect.NULL2, new int[] {1, 2, 3}, "a").toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry template = new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a").toTemplate();

        assertEquals(Effect.NULL1, template.effect());
        assertEquals(1, template.min());
        assertEquals(2, template.max());
        assertEquals(3, template.special());
        assertEquals("a", template.text());
    }
}

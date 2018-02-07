package fr.quatrevieux.araknemu.game.world.item.effect;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UseEffectTest extends TestCase {
    @Test
    void getters() {
        UseEffect effect = new UseEffect(Effect.NULL1, new int[] {1, 2, 3});

        assertEquals(Effect.NULL1, effect.effect());
        assertArrayEquals(new int[] {1, 2, 3}, effect.arguments());
    }

    @Test
    void equalsSameInstance() {
        UseEffect effect = new UseEffect(Effect.NULL1, new int[] {1, 2, 3});

        assertEquals(effect, effect);
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3}),
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3})
        );
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new UseEffect(Effect.NULL1, new int[] {1, 2, 3}), new Object());
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3}),
            new UseEffect(Effect.NULL2, new int[] {1, 2, 3})
        );
    }

    @Test
    void equalsBadArgument() {
        assertNotEquals(
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3}),
            new UseEffect(Effect.NULL1, new int[] {0, 2, 3})
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3}).hashCode(),
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3}).hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new UseEffect(Effect.NULL1, new int[] {1, 2, 3}).hashCode(),
            new UseEffect(Effect.NULL2, new int[] {1, 2, 3}).hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "UseEffect{NULL2:[1, 2, 3]}",
            new UseEffect(Effect.NULL2, new int[] {1, 2, 3}).toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry template = new UseEffect(Effect.NULL1, new int[] {1, 2, 3}).toTemplate();

        assertEquals(Effect.NULL1, template.effect());
        assertEquals(1, template.min());
        assertEquals(2, template.max());
        assertEquals(3, template.special());
        assertEquals("", template.text());
    }
}

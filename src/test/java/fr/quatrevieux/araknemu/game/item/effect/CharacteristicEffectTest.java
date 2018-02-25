package fr.quatrevieux.araknemu.game.item.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacteristicEffectTest {
    @Test
    void getters() {
        CharacteristicEffect effect = new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH);

        assertEquals(Effect.SUB_STRENGTH, effect.effect());
        assertEquals(20, effect.value());
        assertEquals(-20, effect.boost());
        assertEquals(Characteristic.STRENGTH, effect.characteristic());
    }

    @Test
    void equalsSame() {
        CharacteristicEffect effect = new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH);

        assertEquals(effect, effect);
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH), new Object());
    }

    @Test
    void equalsBadValue() {
        assertNotEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 21, -1, Characteristic.STRENGTH),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH)
        );
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new CharacteristicEffect(Effect.ADD_STRENGTH, 20, -1, Characteristic.STRENGTH),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH)
        );
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH)
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).hashCode(),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 21, -1, Characteristic.STRENGTH).hashCode(),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "CharacteristicEffect{SUB_STRENGTH:20}",
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry entry = new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).toTemplate();

        assertEquals(Effect.SUB_STRENGTH, entry.effect());
        assertEquals(20, entry.min());
        assertEquals(0, entry.max());
        assertEquals(0, entry.special());
        assertEquals("0d0+20", entry.text());
    }
}

package fr.quatrevieux.araknemu.game.world.item.effect.mapping;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.world.item.effect.CharacteristicEffect;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class EffectToCharacteristicMappingTest extends TestCase {
    private EffectToCharacteristicMapping mapping = new EffectToCharacteristicMapping();
    
    @Test
    void createNotFound() {
        assertThrows(NoSuchElementException.class, () -> mapping.create(Effect.INFLICT_DAMAGE_FIRE, 0));
    }

    @Test
    void createPositive() {
        CharacteristicEffect effect = mapping.create(Effect.ADD_INTELLIGENCE, 15);

        assertEquals(Effect.ADD_INTELLIGENCE, effect.effect());
        assertEquals(15, effect.value());
        assertEquals(Characteristic.INTELLIGENCE, effect.characteristic());
        assertEquals(15, effect.boost());
    }

    @Test
    void createNegative() {
        CharacteristicEffect effect = mapping.create(Effect.SUB_INTELLIGENCE, 15);

        assertEquals(Effect.SUB_INTELLIGENCE, effect.effect());
        assertEquals(15, effect.value());
        assertEquals(Characteristic.INTELLIGENCE, effect.characteristic());
        assertEquals(-15, effect.boost());
    }

    @Test
    void isNegative() {
        assertTrue(mapping.isNegative(Effect.SUB_AGILITY));
        assertTrue(mapping.isNegative(Effect.SUB_CHANCE));
        assertFalse(mapping.isNegative(Effect.ADD_STRENGTH));
        assertFalse(mapping.isNegative(Effect.ADD_REDUCE_DAMAGE_EARTH));
    }

    @Test
    void createRandomWithoutMaxValue() {
        CharacteristicEffect effect = mapping.createRandom(
            new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 10, 0, 0, "")
        );

        assertEquals(Effect.ADD_AGILITY, effect.effect());
        assertEquals(10, effect.value());
        assertEquals(Characteristic.AGILITY, effect.characteristic());
        assertEquals(10, effect.boost());
    }

    @Test
    void createRandomWithMaxValue() {
        CharacteristicEffect effect = mapping.createRandom(
            new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")
        );

        assertEquals(Effect.ADD_AGILITY, effect.effect());
        assertBetween(0, 100, effect.value());

        assertNotEquals(
            mapping.createRandom(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")).value(),
            mapping.createRandom(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")).value()
        );
    }

    @Test
    void createMax() {
        CharacteristicEffect effect = mapping.createMaximize(
            new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")
        );

        assertEquals(Effect.ADD_AGILITY, effect.effect());
        assertEquals(100, effect.boost());
    }

    @Test
    void createMaxWithNegativeValue() {
        CharacteristicEffect effect = mapping.createMaximize(
            new ItemTemplateEffectEntry(Effect.SUB_AGILITY, 10, 100, 0, "")
        );

        assertEquals(Effect.SUB_AGILITY, effect.effect());
        assertEquals(-10, effect.boost());
    }
}

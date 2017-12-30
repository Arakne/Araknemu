package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacteristicsTransformerTest {
    private CharacteristicsTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new CharacteristicsTransformer();
    }

    @Test
    void serializeSameValue() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.DISCERNMENT, 3000);
        characteristics.set(Characteristic.RESISTANCE_ACTION_POINT, 24);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 3);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertEquals(
            transformer.serialize(characteristics),
            transformer.serialize(characteristics)
        );
    }

    @Test
    void serializeIgnoreNull() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.DISCERNMENT, 3000);
        characteristics.set(Characteristic.RESISTANCE_ACTION_POINT, 0);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 0);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertEquals("9:2to;j:3;", transformer.serialize(characteristics));
    }

    @Test
    void serializeOneStat() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.STRENGTH, 200);

        assertEquals("c:68;", transformer.serialize(characteristics));
    }

    @Test
    void serializeNegativeValue() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.STRENGTH, -200);

        assertEquals("c:-68;", transformer.serialize(characteristics));
    }

    @Test
    void serializeEmpty() {
        assertEquals("", transformer.serialize(new DefaultCharacteristics()));
    }

    @Test
    void serializeNull() {
        assertNull(transformer.serialize(null));
    }

    @Test
    void unserializeNull() {
        assertNull(transformer.unserialize(null));
    }

    @Test
    void unserializeNegativeValue() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.STRENGTH, -200);

        assertEquals(characteristics, transformer.unserialize("c:-68"));
    }

    @Test
    void unserializeEmpty() {
        assertEquals(new DefaultCharacteristics(), transformer.unserialize(""));
    }

    @Test
    void functional() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.DISCERNMENT, 3000);
        characteristics.set(Characteristic.RESISTANCE_ACTION_POINT, 24);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 3);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertEquals(
            characteristics,
            transformer.unserialize(
                transformer.serialize(characteristics)
            )
        );
    }

    @Test
    void serializeRaceStats() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.ACTION_POINT, 6);
        characteristics.set(Characteristic.MOVEMENT_POINT, 3);
        characteristics.set(Characteristic.DISCERNMENT, 100);
        characteristics.set(Characteristic.MAX_SUMMONED_CREATURES, 1);
        characteristics.set(Characteristic.INITIATIVE, 1);

        assertEquals("8:1;9:34;a:6;b:3;j:1;", transformer.serialize(characteristics));

        characteristics.set(Characteristic.DISCERNMENT, 120);

        assertEquals("8:1;9:3o;a:6;b:3;j:1;", transformer.serialize(characteristics));
    }
}
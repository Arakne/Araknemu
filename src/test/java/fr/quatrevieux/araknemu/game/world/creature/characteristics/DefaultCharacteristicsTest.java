package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCharacteristicsTest {
    @Test
    void getSet() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.DISCERNMENT, 5);

        assertEquals(5, characteristics.get(Characteristic.DISCERNMENT));
    }

    @Test
    void getDefault() {
        assertEquals(0, new DefaultCharacteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void equalsBadInstance() {
        assertFalse(new DefaultCharacteristics().equals(new ArrayList<>()));
    }

    @Test
    void equalsTwoEmptyInstances() {
        assertEquals(
            new DefaultCharacteristics(),
            new DefaultCharacteristics()
        );
    }

    @Test
    void equalsNotSameValue() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();
        c1.set(Characteristic.ACTION_POINT, 3);

        DefaultCharacteristics c2 = new DefaultCharacteristics();
        c2.set(Characteristic.ACTION_POINT, 5);

        assertFalse(c1.equals(c2));
    }

    @Test
    void equalsWithSameValues() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();

        c1.set(Characteristic.ACTION_POINT, 3);
        c1.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        DefaultCharacteristics c2 = new DefaultCharacteristics();

        c2.set(Characteristic.ACTION_POINT, 3);
        c2.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        assertEquals(c1, c2);
    }

    @Test
    void equalsWillIgnoreNullValues() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();

        c1.set(Characteristic.ACTION_POINT, 3);
        c1.set(Characteristic.RESISTANCE_ACTION_POINT, 120);
        c1.set(Characteristic.MAX_SUMMONED_CREATURES, 0);
        c1.set(Characteristic.STRENGTH, 0);

        DefaultCharacteristics c2 = new DefaultCharacteristics();

        c2.set(Characteristic.ACTION_POINT, 3);
        c2.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        assertEquals(c1, c2);
    }

    @Test
    void hashCodeEmptyCharacteristics() {
        assertEquals(0, new DefaultCharacteristics().hashCode());
    }

    @Test
    void hashCodeEquals() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();

        c1.set(Characteristic.ACTION_POINT, 3);
        c1.set(Characteristic.RESISTANCE_ACTION_POINT, 120);
        c1.set(Characteristic.MAX_SUMMONED_CREATURES, 0);
        c1.set(Characteristic.STRENGTH, 0);

        DefaultCharacteristics c2 = new DefaultCharacteristics();

        c2.set(Characteristic.ACTION_POINT, 3);
        c2.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void hashCodeMayBeDifferentForNotEqualsValues() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();
        c1.set(Characteristic.ACTION_POINT, 3);

        DefaultCharacteristics c2 = new DefaultCharacteristics();
        c2.set(Characteristic.ACTION_POINT, 5);

        assertNotEquals(
            c1.hashCode(),
            c2.hashCode()
        );
    }
}

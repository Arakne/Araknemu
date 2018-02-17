package fr.quatrevieux.araknemu.data.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacteristicTest {
    @Test
    void fromId() {
        assertEquals(Characteristic.STRENGTH, Characteristic.fromId(10));
        assertEquals(Characteristic.VITALITY, Characteristic.fromId(11));
        assertEquals(Characteristic.WISDOM, Characteristic.fromId(12));
        assertEquals(Characteristic.LUCK, Characteristic.fromId(13));
        assertEquals(Characteristic.AGILITY, Characteristic.fromId(14));
        assertEquals(Characteristic.INTELLIGENCE, Characteristic.fromId(15));
    }

    @Test
    void id() {
        assertEquals(10, Characteristic.STRENGTH.id());
        assertEquals(11, Characteristic.VITALITY.id());
        assertEquals(12, Characteristic.WISDOM.id());
        assertEquals(13, Characteristic.LUCK.id());
        assertEquals(14, Characteristic.AGILITY.id());
        assertEquals(15, Characteristic.INTELLIGENCE.id());
    }
}

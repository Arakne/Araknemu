package fr.quatrevieux.araknemu.network.game.out.info;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformationTest {
    @Test
    void chatFlood() {
        assertEquals(
            "Im0115;1234",
            Information.chatFlood(1234).toString()
        );
    }

    @Test
    void heal() {
        assertEquals(
            "Im01;10",
            Information.heal(10).toString()
        );
    }

    @Test
    void spellLearn() {
        assertEquals(
            "Im03;10",
            Information.spellLearn(10).toString()
        );
    }

    @Test
    void characteristicBoosted() {
        assertEquals("Im09;10", Information.characteristicBoosted(Characteristic.WISDOM, 10).toString());
        assertEquals("Im010;10", Information.characteristicBoosted(Characteristic.STRENGTH, 10).toString());
        assertEquals("Im011;10", Information.characteristicBoosted(Characteristic.LUCK, 10).toString());
        assertEquals("Im012;10", Information.characteristicBoosted(Characteristic.AGILITY, 10).toString());
        assertEquals("Im013;10", Information.characteristicBoosted(Characteristic.VITALITY, 10).toString());
        assertEquals("Im014;10", Information.characteristicBoosted(Characteristic.INTELLIGENCE, 10).toString());

        assertNull(Information.characteristicBoosted(Characteristic.FIXED_DAMAGE, 10));
    }

    @Test
    void cannotPostItemOnChannel() {
        assertEquals(
            "Im0114;",
            Information.cannotPostItemOnChannel().toString()
        );
    }

    @Test
    void positionSaved() {
        assertEquals(
            "Im06;",
            Information.positionSaved().toString()
        );
    }

    @Test
    void bankTaxPayed() {
        assertEquals(
            "Im020;42",
            Information.bankTaxPayed(42).toString()
        );
    }
}

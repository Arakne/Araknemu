package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DamageTest {
    private Damage damage;

    @BeforeEach
    void setUp() {
        damage = new Damage(15);
    }

    @Test
    void defaultValue() {
        assertEquals(15, damage.value());
    }

    @Test
    void fixed() {
        assertEquals(10, damage.fixed(5).value());
    }

    @Test
    void fixedHigherThanValue() {
        assertEquals(0, damage.fixed(20).value());
    }

    @Test
    void percent() {
        assertEquals(12, damage.percent(20).value());
    }

    @Test
    void percentHigherThan100() {
        assertEquals(0, damage.percent(75).percent(30).value());
    }

    @Test
    void fixedAndPercent() {
        assertEquals(7, damage.percent(20).fixed(5).value());
    }

    @Test
    void multiplyPositive() {
        assertEquals(21, damage.percent(20).fixed(5).multiply(3).value());
    }

    @Test
    void multiplyNegative() {
        assertEquals(-7, damage.percent(20).fixed(5).multiply(-1).value());
    }
}

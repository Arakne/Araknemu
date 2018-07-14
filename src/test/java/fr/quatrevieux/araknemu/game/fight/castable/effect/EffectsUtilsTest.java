package fr.quatrevieux.araknemu.game.fight.castable.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectsUtilsTest {
    @Test
    void isDamageEffect() {
        assertTrue(EffectsUtils.isDamageEffect(99));
        assertFalse(EffectsUtils.isDamageEffect(123));
    }

    @Test
    void isLooseApEffect() {
        assertTrue(EffectsUtils.isLooseApEffect(101));
        assertFalse(EffectsUtils.isLooseApEffect(123));
    }
}

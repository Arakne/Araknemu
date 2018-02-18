package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpecialEffectsTest extends TestCase {
    private SpecialEffects effects;

    @BeforeEach
    void setUp() {
        effects = new SpecialEffects();
    }

    @Test
    void add() {
        effects.add(SpecialEffects.Type.PODS, 10);

        assertEquals(10, effects.get(SpecialEffects.Type.PODS));

        effects.add(SpecialEffects.Type.PODS, 15);

        assertEquals(25, effects.get(SpecialEffects.Type.PODS));
    }

    @Test
    void sub() {
        effects.sub(SpecialEffects.Type.PODS, 10);

        assertEquals(-10, effects.get(SpecialEffects.Type.PODS));

        effects.sub(SpecialEffects.Type.PODS, 15);

        assertEquals(-25, effects.get(SpecialEffects.Type.PODS));
    }

    @Test
    void clear() {
        effects.add(SpecialEffects.Type.PODS, 10);
        effects.clear();

        assertEquals(0, effects.get(SpecialEffects.Type.PODS));
    }
}

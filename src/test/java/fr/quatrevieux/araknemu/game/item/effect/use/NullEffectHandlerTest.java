package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullEffectHandlerTest extends TestCase {
    @Test
    void instance() {
        assertInstanceOf(NullEffectHandler.class, NullEffectHandler.INSTANCE);
    }

    @Test
    void check() {
        assertTrue(NullEffectHandler.INSTANCE.check(null, null));
        assertTrue(NullEffectHandler.INSTANCE.checkTarget(null, null, null, 0));
        assertTrue(NullEffectHandler.INSTANCE.checkFighter(null, null));
    }

    @Test
    void apply() {
        NullEffectHandler.INSTANCE.apply(null, null);
        NullEffectHandler.INSTANCE.applyToTarget(null, null, null, 0);
    }
}
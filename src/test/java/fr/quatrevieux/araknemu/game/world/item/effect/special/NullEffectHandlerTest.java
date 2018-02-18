package fr.quatrevieux.araknemu.game.world.item.effect.special;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullEffectHandlerTest extends TestCase {
    @Test
    void create() {
        assertEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new NullEffectHandler().create(new ItemTemplateEffectEntry(Effect.NULL1, 1, 2, 3, "a"), false)
        );
    }

    @Test
    void instance() {
        assertInstanceOf(NullEffectHandler.class, NullEffectHandler.INSTANCE);
    }
}

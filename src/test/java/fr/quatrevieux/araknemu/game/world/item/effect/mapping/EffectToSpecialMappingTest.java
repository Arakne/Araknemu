package fr.quatrevieux.araknemu.game.world.item.effect.mapping;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectToSpecialMappingTest extends TestCase {
    @Test
    void create() {
        assertEquals(
            new SpecialEffect(Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new EffectToSpecialMapping().create(new ItemTemplateEffectEntry(Effect.NULL1, 1, 2, 3, "a"))
        );
    }
}

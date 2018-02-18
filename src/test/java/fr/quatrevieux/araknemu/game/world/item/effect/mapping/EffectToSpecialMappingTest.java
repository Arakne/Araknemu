package fr.quatrevieux.araknemu.game.world.item.effect.mapping;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.special.AddSpecialEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.special.NullEffectHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectToSpecialMappingTest extends TestCase {
    @Test
    void create() {
        assertEquals(
            new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {1, 2, 3}, "a"),
            new EffectToSpecialMapping().create(new ItemTemplateEffectEntry(Effect.NULL1, 1, 2, 3, "a"), false)
        );
    }

    @Test
    void createFromRegisteredHandler() {
        assertEquals(
            new SpecialEffect(new AddSpecialEffect(SpecialEffects.Type.PODS), Effect.ADD_PODS, new int[] {200, 0, 0}, "0d0+200"),
            new EffectToSpecialMapping().create(new ItemTemplateEffectEntry(Effect.ADD_PODS, 0, 200, 0, ""), true)
        );
    }
}

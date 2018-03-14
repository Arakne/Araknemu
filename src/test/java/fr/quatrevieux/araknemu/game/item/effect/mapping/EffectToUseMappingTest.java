package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EffectToUseMappingTest extends GameBaseCase {
    private EffectToUseMapping mapping;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        mapping = new EffectToUseMapping(
            container.get(SpellService.class)
        );
    }

    @Test
    void createEffectNotHandled() {
        UseEffect effect = mapping.create(new ItemTemplateEffectEntry(Effect.INVOKE_CAPTURE, 1, 2, 3, ""));

        assertEquals(Effect.INVOKE_CAPTURE, effect.effect());
        assertArrayEquals(new int[] {1, 2, 3}, effect.arguments());
    }

    @Test
    void createEffectHandled() {
        UseEffect effect = mapping.create(new ItemTemplateEffectEntry(Effect.ADD_CHARACT_AGILITY, 1, 2, 0, ""));

        assertEquals(Effect.ADD_CHARACT_AGILITY, effect.effect());
        assertArrayEquals(new int[] {1, 2, 0}, effect.arguments());
    }

    @Test
    void createFromList() {
        List<UseEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.ADD_LIFE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.SUB_INITIATIVE, 32, 0, 0, "")
            )
        );

        assertCount(1, effects);
        assertEquals(Effect.ADD_LIFE, effects.get(0).effect());
        assertArrayEquals(new int[] {10, 100, 0}, effects.get(0).arguments());
    }
}

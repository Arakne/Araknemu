package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectToUseMappingTest extends GameBaseCase {
    private EffectToUseMapping mapping;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        mapping = new EffectToUseMapping();
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
}

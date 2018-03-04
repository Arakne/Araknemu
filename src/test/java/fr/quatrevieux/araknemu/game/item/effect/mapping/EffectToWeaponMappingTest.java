package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectToWeaponMappingTest extends TestCase {
    private EffectToWeaponMapping mapping;

    @BeforeEach
    void setUp() {
        mapping = new EffectToWeaponMapping();
    }

    @Test
    void create() {
        assertEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0),
            new EffectToWeaponMapping().create(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0, "1d6+9"))
        );
    }

    @Test
    void createFromList() {
        List<WeaponEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.STOLEN_FIRE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.SUB_INITIATIVE, 32, 0, 0, "")
            )
        );

        assertCount(1, effects);
        assertEquals(Effect.STOLEN_FIRE, effects.get(0).effect());
        assertEquals(10, effects.get(0).min());
        assertEquals(100, effects.get(0).max());
    }
}
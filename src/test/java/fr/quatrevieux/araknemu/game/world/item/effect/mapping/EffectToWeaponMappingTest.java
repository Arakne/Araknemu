package fr.quatrevieux.araknemu.game.world.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.world.item.effect.WeaponEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectToWeaponMappingTest {
    @Test
    void create() {
        assertEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0),
            new EffectToWeaponMapping().create(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0, "1d6+9"))
        );
    }
}
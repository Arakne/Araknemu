package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectMappersTest extends GameBaseCase {
    @Test
    void get() {
        EffectToCharacteristicMapping m1 = new EffectToCharacteristicMapping();
        EffectToWeaponMapping m2 = new EffectToWeaponMapping();

        EffectMappers mappers = new EffectMappers(m1, m2);

        assertSame(m1, mappers.get(CharacteristicEffect.class));
        assertSame(m2, mappers.get(WeaponEffect.class));
    }
}

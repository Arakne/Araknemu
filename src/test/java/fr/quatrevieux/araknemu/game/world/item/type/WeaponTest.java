package fr.quatrevieux.araknemu.game.world.item.type;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.WeaponEffect;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WeaponTest extends TestCase {
    @Test
    void getters() {
        Weapon weapon = new Weapon(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
            Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            new ArrayList<>()
        );

        assertEquals(40, weapon.template().id());
        assertEquals(Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)), weapon.weaponEffects());
        assertEquals(Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)), weapon.characteristics());
    }

    @Test
    void effects() {
        Weapon weapon = new Weapon(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
            Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            new ArrayList<>()
        );

        assertEquals(
            Arrays.asList(
                new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0),
                new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)
            ),
            weapon.effects()
        );
    }

    @Test
    void equalsBadType() {
        assertNotEquals(
            new Weapon(
                new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>()
            ),
            new Object()
        );
    }

    @Test
    void equalsBadWeaponEffect() {
        assertNotEquals(
            new Weapon(
                new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>()
            ),
            new Weapon(
                new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_EARTH, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>()
            )
        );
    }

    @Test
    void equalsOk() {
        assertEquals(
            new Weapon(
                new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>()
            ),
            new Weapon(
                new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>()
            )
        );
    }
}

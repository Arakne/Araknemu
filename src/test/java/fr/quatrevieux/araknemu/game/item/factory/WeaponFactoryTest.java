package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToWeaponMapping;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WeaponFactoryTest extends GameBaseCase {
    private WeaponFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new WeaponFactory(
            new EffectToWeaponMapping(),
            new EffectToCharacteristicMapping(),
            new EffectToSpecialMapping()
        );
    }

    @Test
    void createSimple() {
        Item item = factory.create(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), null, false
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(1, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.weaponEffects());
        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, weapon.weaponEffects().get(0).effect());
        assertEquals(1, weapon.weaponEffects().get(0).min());
        assertEquals(7, weapon.weaponEffects().get(0).max());
    }

    @Test
    void createWithRandomCharacteristic() {
        Item item = factory.create(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), null, false
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.characteristics());
        assertEquals(Effect.ADD_STRENGTH, weapon.characteristics().get(0).effect());
        assertTrue(weapon.characteristics().get(0).value() >= 1);
        assertTrue(weapon.characteristics().get(0).value() <= 10);
    }

    @Test
    void createWithMaxCharacteristic() {
        Item item = factory.create(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), null, true
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.characteristics());
        assertEquals(Effect.ADD_STRENGTH, weapon.characteristics().get(0).effect());
        assertEquals(10, weapon.characteristics().get(0).value());
    }

    @Test
    void createWithSpecialEffect() {
        Item item = factory.create(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                    new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), null, true
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.specials());
        assertEquals(Effect.NULL1, weapon.specials().get(0).effect());
    }

    @Test
    void retrieve() {
        Item item = factory.retrieve(
            new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ),
            null,
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 20, 0, 0, "")
            )
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.weaponEffects());
        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, weapon.weaponEffects().get(0).effect());
        assertEquals(1, weapon.weaponEffects().get(0).min());
        assertEquals(7, weapon.weaponEffects().get(0).max());

        assertCount(1, weapon.characteristics());
        assertEquals(Effect.ADD_STRENGTH, weapon.characteristics().get(0).effect());
        assertEquals(20, weapon.characteristics().get(0).value());
    }
}

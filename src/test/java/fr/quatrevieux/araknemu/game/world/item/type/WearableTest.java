package fr.quatrevieux.araknemu.game.world.item.type;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.special.NullEffectHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WearableTest {
    @Test
    void getters() {
        Wearable wearable = new Wearable(
            new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            new ArrayList<>()
        );

        assertEquals(39, wearable.template().id());
        assertEquals(Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)), wearable.characteristics());
    }

    @Test
    void equalsBadType() {
        assertNotEquals(
            new Wearable(
                new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>()
            ),
            new Object()
        );
    }

    @Test
    void equalsBadCharacteristics() {
        assertNotEquals(
            new Wearable(
                new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>()
            ),new Wearable(
                new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 3, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>()
            )
        );
    }

    @Test
    void equalsOk() {
        assertEquals(
            new Wearable(
                new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>()
            ),
            new Wearable(
                new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>()
            )
        );
    }

    @Test
    void effects() {
        Wearable wearable = new Wearable(
            new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            Arrays.asList(new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, ""))
        );

        assertEquals(
            Arrays.asList(
                new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE),
                new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, "")
            ),
            wearable.effects()
        );
    }

    @Test
    void apply() {
        Wearable wearable = new Wearable(
            new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            Arrays.asList(new SpecialEffect(NullEffectHandler.INSTANCE, Effect.NULL1, new int[] {0, 0, 0}, ""))
        );

        MutableCharacteristics characteristics = new DefaultCharacteristics();
        characteristics.set(Characteristic.INTELLIGENCE, 5);

        wearable.apply(characteristics);

        assertEquals(7, characteristics.get(Characteristic.INTELLIGENCE));
    }
}

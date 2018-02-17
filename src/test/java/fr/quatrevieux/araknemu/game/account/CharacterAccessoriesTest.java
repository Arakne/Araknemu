package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.NullAccessory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CharacterAccessoriesTest extends TestCase {
    @Test
    void getNotFound() {
        CharacterAccessories accessories = new CharacterAccessories(Collections.emptyList());

        assertInstanceOf(NullAccessory.class, accessories.get(AccessoryType.HELMET));
        assertEquals(AccessoryType.HELMET, accessories.get(AccessoryType.HELMET).type());
    }

    @Test
    void get() {
        CharacterAccessories accessories = new CharacterAccessories(
            Arrays.asList(
                new PlayerItem(1, 1, 123, null, 1, 1),
                new PlayerItem(1, 1, 456, null, 1, 6),
                new PlayerItem(1, 1, 789, null, 1, 7)
            )
        );

        assertInstanceOf(CharacterAccessory.class, accessories.get(AccessoryType.WEAPON));
        assertInstanceOf(CharacterAccessory.class, accessories.get(AccessoryType.HELMET));
        assertInstanceOf(CharacterAccessory.class, accessories.get(AccessoryType.MANTLE));

        assertEquals(123, accessories.get(AccessoryType.WEAPON).appearance());
        assertEquals(456, accessories.get(AccessoryType.HELMET).appearance());
        assertEquals(789, accessories.get(AccessoryType.MANTLE).appearance());
    }
}

package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddItemTest {
    @Test
    void simpleItem() {
        assertEquals(
            "OAKOc~11c~5~~",
            new AddItem(
                new InventoryEntry(
                    null,
                    new PlayerItem(1, 12, 284, new ArrayList<>(), 5, -1),
                    new Resource(
                        new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10),
                        new ItemType(48, "Poudre", SuperType.RESOURCE, null),
                        new ArrayList<>()
                    )
                )
            ).toString()
        );
    }

    @Test
    void wearedItemWithStats() {
        assertEquals(
            "OAKOc~27~5~0~7e#2#0#0#0d0+2",
            new AddItem(
                new InventoryEntry(
                    null,
                    new PlayerItem(1, 12, 39, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 5, 0),
                    new Wearable(
                        new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                        new ItemType(1, "Amulette", SuperType.AMULET, null),
                        null,
                        Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, 1, Characteristic.INTELLIGENCE)),
                        new ArrayList<>()
                    )
                )
            ).toString()
        );
    }
}

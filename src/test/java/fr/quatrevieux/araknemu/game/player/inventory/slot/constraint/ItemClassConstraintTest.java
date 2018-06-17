package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemClassConstraintTest extends GameBaseCase {
    private ItemClassConstraint constraint = new ItemClassConstraint(Wearable.class);

    @Test
    void success() throws InventoryException {
        constraint.check(
            new Wearable(
                new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
                new ItemType(48, "Poudre", SuperType.RESOURCE, null),
                null,
                new ArrayList<>(),
                new ArrayList<>()
            ),
            1
        );
    }

    @Test
    void error() {
        assertThrows(
            InventoryException.class,
            () -> constraint.check(
                new Resource(
                    new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10),
                    new ItemType(48, "Poudre", SuperType.RESOURCE, null),
                    new ArrayList<>()
                ),
                1
            ),
            "Bad item class"
        );
    }
}

package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ItemClassConstraintTest extends GameBaseCase {
    private ItemClassConstraint constraint = new ItemClassConstraint(Wearable.class);

    @Test
    void success() throws InventoryException {
        constraint.check(
            new Wearable(
                new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
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
                    new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10),
                    new ArrayList<>()
                ),
                1
            ),
            "Bad item class"
        );
    }
}

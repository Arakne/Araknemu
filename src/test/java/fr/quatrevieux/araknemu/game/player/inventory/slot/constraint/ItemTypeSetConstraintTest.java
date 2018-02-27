package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTypeSetConstraintTest extends GameBaseCase {
    private ItemTypeSetConstraint constraint;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        constraint = new ItemTypeSetConstraint(Type.AMULETTE, Type.EPEE);
    }

    @Test
    void success() throws ContainerException, InventoryException {
        constraint.check(container.get(ItemService.class).create(40), 1);
        constraint.check(container.get(ItemService.class).create(39), 1);
    }

    @Test
    void fail() throws ContainerException {
        assertThrows(InventoryException.class, () -> constraint.check(container.get(ItemService.class).create(284), 1), "Bad item type");
    }
}

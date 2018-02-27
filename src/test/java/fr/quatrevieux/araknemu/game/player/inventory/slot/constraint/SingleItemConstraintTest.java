package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class SingleItemConstraintTest {
    private SingleItemConstraint constraint = new SingleItemConstraint();

    @Test
    void success() throws InventoryException {
        constraint.check(Mockito.mock(Item.class), 1);
    }

    @Test
    void fail() {
        assertThrows(InventoryException.class, () -> constraint.check(Mockito.mock(Item.class), 2), "Invalid quantity");
    }
}
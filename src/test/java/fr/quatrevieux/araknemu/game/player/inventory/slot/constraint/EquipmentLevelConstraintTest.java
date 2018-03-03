package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentLevelConstraintTest extends GameBaseCase {
    private EquipmentLevelConstraint constraint;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushHighLevelItems()
            .pushItemSets()
        ;

        constraint = new EquipmentLevelConstraint(gamePlayer());
    }

    @Test
    void checkError() throws ContainerException, InventoryException {
        try {
            constraint.check(container.get(ItemService.class).create(112425), 1);

            fail("BadLevelException should be thrown");
        } catch (BadLevelException e) {
            assertEquals(200, e.level());
        }
    }

    @Test
    void checkSuccess() throws ContainerException, InventoryException {
        constraint.check(container.get(ItemService.class).create(2425), 1);
    }
}

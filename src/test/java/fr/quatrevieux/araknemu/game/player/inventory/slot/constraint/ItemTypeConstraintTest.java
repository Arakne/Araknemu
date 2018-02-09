package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.item.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTypeConstraintTest extends GameBaseCase {
    private ItemTypeConstraint constraint = new ItemTypeConstraint(Type.AMULETTE);

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();
    }

    @Test
    void success() throws ContainerException {
        assertTrue(
            constraint.check(
                container.get(ItemService.class).create(2425),
                1
            )
        );
    }

    @Test
    void fail() throws ContainerException {
        assertFalse(
            constraint.check(
                container.get(ItemService.class).create(40),
                1
            )
        );
    }
}
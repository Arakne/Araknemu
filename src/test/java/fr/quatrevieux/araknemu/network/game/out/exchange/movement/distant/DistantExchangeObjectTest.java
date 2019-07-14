package fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistantExchangeObjectTest extends GameBaseCase {
    private ItemEntry entry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        entry = explorationPlayer().inventory().add(
            container.get(ItemService.class).create(2422, true),
            3
        );
    }

    @Test
    void generateAdd() {
        assertEquals(
            "EmKO+1|2|2422|8a#f#0#0#0d0+15,7d#21#0#0#0d0+33",
            new DistantExchangeObject(entry, 2).toString()
        );
    }

    @Test
    void generateRemove() {
        assertEquals(
            "EmKO-1",
            new DistantExchangeObject(entry, 0).toString()
        );
    }
}

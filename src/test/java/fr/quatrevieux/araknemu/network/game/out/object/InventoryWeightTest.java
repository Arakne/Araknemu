package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryWeightTest extends GameBaseCase {
    private GamePlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        player = gamePlayer(true);
    }

    @Test
    void generate() throws ContainerException {
        player.inventory().add(container.get(ItemService.class).create(2411), 10);

        assertEquals("Ow100|1250", new InventoryWeight(player).toString());
    }
}
package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharacterSelectedTest extends GameBaseCase {
    @Test
    void generate() throws ContainerException, SQLException {
        assertEquals("ASK|1|Bob|50||0|10|7b|1c8|315|", new CharacterSelected(gamePlayer()).toString());
    }

    @Test
    void generateWithItems() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        gamePlayer().inventory().add(container.get(ItemService.class).create(39), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(40), 1, 1);
        gamePlayer().inventory().add(container.get(ItemService.class).create(284), 10, -1);

        assertEquals("ASK|1|Bob|50||0|10|7b|1c8|315|1~27~1~0~7e#2#0#0#0d0+2;2~28~1~1~64#1#7#0#1d7+0;3~11c~a~~", new CharacterSelected(gamePlayer()).toString());
    }
}

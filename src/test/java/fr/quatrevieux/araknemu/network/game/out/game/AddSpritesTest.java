package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddSpritesTest extends GameBaseCase {
    @Test
    void generateWithPlayer() throws Exception {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        ExplorationPlayer p1 = explorationPlayer();
        ExplorationPlayer p2 = new ExplorationPlayer(makeOtherPlayer());

        p1.inventory().add(container.get(ItemService.class).create(2416), 1, 1);

        assertEquals(
            "GM|+279;0;0;1;Bob;1;10^100x100;0;;7b;1c8;315;970,,,,;;;;;;c;|+210;0;0;2;Other;9;90^100x100;0;;-1;-1;-1;,,,,;;;;;;c;",
            new AddSprites(Arrays.asList(p1.sprite(), p2.sprite())).toString()
        );
    }
}

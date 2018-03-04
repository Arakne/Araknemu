package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsTest extends GameBaseCase {
    @Test
    void generateWithOnlyClassStats() throws Exception {
        dataSet.pushRaces();

        GamePlayer player = makeOtherPlayer();

        player.characteristics().setBoostPoints(13);

        assertEquals(
            "As0,0,110|1000|13|10||50,50|0,10000|12|100|6,0,0,0|3,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player).toString()
        );
    }

    @Test
    void generateNotFullLife() throws Exception {
        dataSet.pushRaces();

        GamePlayer player = makeOtherPlayer();
        player.life().set(20);

        assertEquals(
            "As0,0,110|1000|0|10||20,50|0,10000|4|100|6,0,0,0|3,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player).toString()
        );
    }

    @Test
    void generateWithStuffStatsAndSpecials() throws Exception {
        dataSet.pushRaces();
        dataSet.pushItemTemplates();
        dataSet.pushItemSets();

        GamePlayer player = makeOtherPlayer(10);

        player.characteristics().setBoostPoints(13);
        player.inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        player.inventory().add(container.get(ItemService.class).create(2414, true), 1, 7);
        player.characteristics().rebuildSpecialEffects();
        player.characteristics().rebuildStuffStats();
        player.life().rebuild();

        assertEquals(
            "As0,0,110|1000|13|10||143,143|0,10000|365|100|6,0,0,0|3,0,0,0|0,15,0,0|0,48,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,15,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player).toString()
        );
    }
}

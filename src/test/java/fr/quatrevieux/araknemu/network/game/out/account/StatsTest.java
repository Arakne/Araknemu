/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsTest extends GameBaseCase {
    @Test
    void generateWithOnlyClassStats() throws Exception {
        dataSet.pushRaces();

        GamePlayer player = makeOtherPlayer();

        Player entity = readField(player, "entity");

        entity.setBoostPoints(13);
        entity.setSpellPoints(7);

        assertEquals(
            "As0,0,110|0|13|7||50,50|10000,10000|12|100|6,0,0,0|3,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player.properties()).toString()
        );
    }

    @Test
    void generateNotFullLife() throws Exception {
        dataSet.pushRaces();

        GamePlayer player = makeOtherPlayer();
        player.properties().life().set(20);

        assertEquals(
            "As0,0,110|0|0|0||20,50|10000,10000|4|100|6,0,0,0|3,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player.properties()).toString()
        );
    }

    @Test
    void generateWithStuffStatsAndSpecials() throws Exception {
        dataSet.pushRaces();
        dataSet.pushItemTemplates();
        dataSet.pushItemSets();

        GamePlayer player = makeOtherPlayer(10);

        this.<Player>readField(player, "entity").setBoostPoints(13);
        player.inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        player.inventory().add(container.get(ItemService.class).create(2414, true), 1, 7);
        player.properties().characteristics().rebuildSpecialEffects();
        player.properties().characteristics().rebuildStuffStats();
        player.properties().life().rebuild();

        assertEquals(
            "As0,19200,25200|0|13|0||143,143|10000,10000|365|100|6,0,0,0|3,0,0,0|0,15,0,0|0,48,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,15,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player.properties()).toString()
        );
    }

    @Test
    void generateWithXp() throws Exception {
        dataSet.pushRaces();

        GamePlayer player = makeOtherPlayer();
        player.properties().experience().add(19250);
        player.properties().life().rebuild();

        assertEquals(
            "As19250,19200,25200|0|45|9||95,95|10000,10000|23|100|6,0,0,0|3,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player.properties()).toString()
        );
    }

    @Test
    void generateWithKamas() throws Exception {
        dataSet.pushRaces();

        GamePlayer player = makeOtherPlayer();
        player.inventory().addKamas(23145);

        assertEquals(
            "As0,0,110|23145|0|0||50,50|10000,10000|12|100|6,0,0,0|3,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(player.properties()).toString()
        );
    }

    @Test
    void forPlayerFighter() throws SQLException, ContainerException {
        PlayerFighter fighter = new PlayerFighter(gamePlayer(true));

        assertEquals(
            "As5481459,5350000,5860000|15225|0|0||295,295|10000,10000|273|100|6,0,0,0|3,0,0,0|50,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|150,0,0,0|0,0,0,0|1,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|0,0,0,0|",
            new Stats(fighter.properties()).toString()
        );
    }
}

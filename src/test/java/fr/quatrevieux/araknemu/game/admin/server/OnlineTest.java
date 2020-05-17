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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OnlineTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Online(container.get(PlayerService.class), container.get(ExplorationMapService.class));
        dataSet
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;
    }

    @Test
    void executeWithoutArguments() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online");

        assertOutput(
            "There is 2 online players",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] joining game - <u><a href='asfunction:onHref,ExecCmd,${player:Bob} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Other'>Other</a></u> Cra [-51,10] joining game - <u><a href='asfunction:onHref,ExecCmd,${player:Other} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Other,true'>goto</a></u>"
        );
    }

    @Test
    void executeWithExploringPlayer() throws Exception {
        explorationPlayer();

        execute("online");

        assertOutput(
            "There is 1 online players",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-4,3] in exploration - <u><a href='asfunction:onHref,ExecCmd,${player:Bob} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>"
        );
    }

    @Test
    void executeWithFightingPlayer() throws Exception {
        GamePlayer player = gamePlayer(true);
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        container.get(FightService.class).handler(ChallengeBuilder.class).start(challengeBuilder -> challengeBuilder
            .fighter(player)
            .fighter(player)
            .map(map)
        );

        execute("online");

        assertOutput(
            "There is 1 online players",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] in combat - <u><a href='asfunction:onHref,ExecCmd,${player:Bob} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>"
        );
    }

    @Test
    void executeFilterByNameOneMatch() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online", "b");

        assertOutput(
            "There is 2 online players",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] joining game - <u><a href='asfunction:onHref,ExecCmd,${player:Bob} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>"
        );
    }

    @Test
    void executeFilterByNameTwoMatch() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online", "o");

        assertOutput(
            "There is 2 online players",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] joining game - <u><a href='asfunction:onHref,ExecCmd,${player:Bob} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Other'>Other</a></u> Cra [-51,10] joining game - <u><a href='asfunction:onHref,ExecCmd,${player:Other} info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Other,true'>goto</a></u>"
        );
    }

    @Test
    void executeFilterByNameNoneMatch() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online", "not_found");

        assertOutput("There is 2 online players");
    }

    @Test
    void help() {
        String help = command.help();

        assertTrue(help.contains("List online players"));
        assertTrue(help.contains("online [search]"));
        assertTrue(help.contains("${server} online john"));
    }
}

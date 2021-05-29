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

import fr.quatrevieux.araknemu.game.GameService;
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

        command = new Online(container.get(PlayerService.class), container.get(ExplorationMapService.class), container.get(GameService.class));
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
            "There is 2 online players with 2 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] joining game [127.0.0.1] - <u><a href='asfunction:onHref,ExecCmd,@Bob info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Other'>Other</a></u> Cra [-51,10] joining game [no ip] - <u><a href='asfunction:onHref,ExecCmd,@Other info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Other,true'>goto</a></u>"
        );
    }

    @Test
    void executeWithExploringPlayer() throws Exception {
        explorationPlayer();

        execute("online");

        assertOutput(
            "There is 1 online players with 1 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-4,3] in exploration [127.0.0.1] - <u><a href='asfunction:onHref,ExecCmd,@Bob info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>"
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
            "There is 1 online players with 1 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] in combat [127.0.0.1] - <u><a href='asfunction:onHref,ExecCmd,@Bob info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>"
        );
    }

    @Test
    void executeFilterByNameOneMatch() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online", "b");

        assertOutput(
            "There is 2 online players with 2 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] joining game [127.0.0.1] - <u><a href='asfunction:onHref,ExecCmd,@Bob info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>"
        );
    }

    @Test
    void executeFilterByNameTwoMatch() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online", "o");

        assertOutput(
            "There is 2 online players with 2 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-51,10] joining game [127.0.0.1] - <u><a href='asfunction:onHref,ExecCmd,@Bob info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Other'>Other</a></u> Cra [-51,10] joining game [no ip] - <u><a href='asfunction:onHref,ExecCmd,@Other info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Other,true'>goto</a></u>"
        );
    }

    @Test
    void executeFilterByNameNoneMatch() throws Exception {
        gamePlayer(true);
        makeOtherPlayer();

        execute("online", "not_found");

        assertOutput(
            "There is 2 online players with 2 active sessions",
            "No results found"
        );
    }

    @Test
    void limit() throws Exception {
        explorationPlayer();
        makeOtherPlayer();

        execute("online", "--limit", "1");

        assertOutput(
            "There is 2 online players with 2 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Bob'>Bob</a></u> Feca [-4,3] in exploration [127.0.0.1] - <u><a href='asfunction:onHref,ExecCmd,@Bob info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Bob,true'>goto</a></u>",
            "------------------------------------------------\n" +
            "\t<b><u><a href='asfunction:onHref,ExecCmd,*online --limit 1 --skip 1,true'>next</a></u></b>"
        );
    }

    @Test
    void skip() throws Exception {
        explorationPlayer();
        makeOtherPlayer();

        execute("online", "--skip", "1");

        assertOutput(
            "There is 2 online players with 2 active sessions",
            "<u><a href='asfunction:onHref,ShowPlayerPopupMenu,Other'>Other</a></u> Cra [-51,10] joining game [no ip] - <u><a href='asfunction:onHref,ExecCmd,@Other info,true'>info</a></u> <u><a href='asfunction:onHref,ExecCmd,goto player Other,true'>goto</a></u>"
        );
        execute("online", "--skip", "10");

        assertOutput(
            "There is 2 online players with 2 active sessions",
            "No results found"
        );
    }

    @Test
    void help() {
        assertHelp(
            "online - List online players",
            "========================================",
            "SYNOPSIS",
                "\tonline [SEARCH] [--limit N=20] [--skip N]",
            "OPTIONS",
                "\tSEARCH : Optional. Filter the online player name. Return only players containing the search term into the name.",
                "\t--limit : Limit the number of returned lines. By default the limit is set to 20.",
                "\t--skip : Skip the first lines.",
            "EXAMPLES",
                "\t*online                      - List all online players",
                "\t*online john                 - List all online players, containing john in the name",
                "\t*online --skip 3 --limit 5 j - With pagination",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}

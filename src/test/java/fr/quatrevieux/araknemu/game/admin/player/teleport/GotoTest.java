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

package fr.quatrevieux.araknemu.game.admin.player.teleport;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GotoTest  extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushAreas()
            .pushSubAreas()
            .pushMaps()
        ;

        command = new Goto(gamePlayer(true), container.get(ExplorationMapService.class), new LocationResolver[]{
            new PositionResolver(gamePlayer(), container.get(GeolocationService.class)),
            new MapResolver(container.get(ExplorationMapService.class)),
            new PlayerResolver(container.get(PlayerService.class), container.get(ExplorationMapService.class)),
            new CellResolver(),
        });
    }

    @Test
    void executeMapOnExplorationPlayer() throws ContainerException, SQLException, AdminException {
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "map", "10340");
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
        assertOutput("Teleport Bob to [3,6] (10340) at cell 15");
    }

    @Test
    void executePositionOnExplorationPlayer() throws ContainerException, SQLException, AdminException {
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "position", "3;6");
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
        assertOutput("Teleport Bob to [3,6] (10340) at cell 15");
    }

    @Test
    void executePlayerOnExplorationPlayer() throws Exception {
        ExplorationPlayer other = makeOtherExplorationPlayer();
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "player", other.player().name());
        player.interactions().end(1);

        assertEquals(10540, player.map().id());
        assertOutput("Teleport Bob to [-51,10] (10540) at cell 15");
    }

    @Test
    void executeCellOnExplorationPlayer() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "cell", "266");
        player.interactions().end(1);

        assertEquals(10300, player.map().id());
        assertEquals(266, player.position().cell());
        assertOutput("Teleport Bob to [-4,3] (10300) at cell 266");
    }

    @Test
    void executeMapAndCell() throws ContainerException, SQLException, AdminException {
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "map", "10340", "cell", "42");
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
        assertEquals(42, player.position().cell());
        assertOutput("Teleport Bob to [3,6] (10340) at cell 42");
    }

    @Test
    void executeNotExploring() throws ContainerException, SQLException, AdminException {
        assertThrows(AdminException.class, () -> execute("goto", "map", "10340"));

        assertEquals(10540, gamePlayer().position().map());
        assertEquals(200, gamePlayer().position().cell());
    }

    @Test
    void executeBusy() throws ContainerException, SQLException {
        ExplorationPlayer player = explorationPlayer();
        player.interactions().start(new Interaction() {
            @Override
            public Interaction start() {
                return this;
            }

            @Override
            public void stop() {

            }
        });

        assertThrows(AdminException.class, () -> execute("goto", "map", "10340"));

        assertEquals(10300, gamePlayer().position().map());
        assertEquals(279, gamePlayer().position().cell());
    }

    @Test
    void executeNotExploringForce() throws ContainerException, SQLException, AdminException {
        execute("goto", "map", "10340", "--force");

        assertEquals(10340, gamePlayer().position().map());
        assertOutput(
            "Player is not in exploration. Define the position for the next exploration session.",
            "Teleport Bob to [3,6] (10340) at cell 200"
        );
    }

    @Test
    void executeBusyForce() throws ContainerException, SQLException, AdminException {
        ExplorationPlayer player = explorationPlayer();
        player.interactions().start(new Interaction() {
            @Override
            public Interaction start() {
                return this;
            }

            @Override
            public void stop() {

            }
        });

        execute("goto", "map", "10340", "--force");
        assertFalse(player.interactions().interacting());

        player.interactions().end(1);

        assertEquals(10340, player.map().id());
        assertEquals(15, player.position().cell());

        assertOutput(
            "Player is busy. Stop all there interactions.",
            "Teleport Bob to [3,6] (10340) at cell 15"
        );
    }

    @Test
    void executeAutoResolveMap() throws ContainerException, SQLException, AdminException {
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "10340");
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
    }

    @Test
    void executeAutoResolvePosition() throws ContainerException, SQLException, AdminException {
        ExplorationPlayer player = explorationPlayer();

        execute("goto", "3;6");
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
    }

    @Test
    void executeAutoResolvePlayer() throws Exception {
        ExplorationPlayer other = makeOtherExplorationPlayer();
        ExplorationPlayer player = explorationPlayer();

        execute("goto", other.player().name());
        player.interactions().end(1);

        assertEquals(10540, player.map().id());
    }

    @Test
    void invalidArguments() throws SQLException {
        explorationPlayer();

        assertThrows(AdminException.class, () -> execute("goto", "map"));
        assertThrows(AdminException.class, () -> execute("goto", "map", "invalid"));
        assertThrows(AdminException.class, () -> execute("goto", "invalid"));
    }

    @Test
    void help() {
        assertHelp(
            "goto - Teleport the player to the desired location",
            "========================================",
            "SYNOPSIS",
                "\tgoto [TYPE TARGET ...] [--force]",
            "OPTIONS",
                "\tTYPE : Define the target type (available types are defined bellow). If not provided, will try all available resolvers.",
                "\tTARGET : Required. The target. This value depends of the type.",
                "\tTYPE: position : Resolve by map position.",
                    "\t\tUsage: goto position [x];[y]",
                "\tTYPE: map : Resolve by map id.",
                    "\t\tUsage: goto map [mapid]",
                "\tTYPE: player : Teleport to the player.",
                    "\t\tUsage: goto player [name]",
                "\tTYPE: cell : Define the target cell.",
                    "\t\tUsage: goto [map target] cell [cellid]",
                "\t--force : Force the teleporation even if the player is busy or in fight.",
            "EXAMPLES",
                "\tgoto map 10340         - Teleport to the map id 10340",
                "\tgoto map 10340 cell 45 - Teleport to the map id 10340 at cell 45",
                "\tgoto player John       - Teleport to the John's map",
                "\tgoto position 3;5      - Teleport by geolocation",
                "\t@John goto map 10340   - Teleport John to map id 10340",
                "\t@John goto player Alan - Teleport John to the Alan's map",
                "\tgoto 3;5 cell 42       - Command can works without [type] argument, if not ambiguous",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}

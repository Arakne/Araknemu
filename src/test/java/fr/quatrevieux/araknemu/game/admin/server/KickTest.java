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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class KickTest extends CommandTestCase {
    private int id = 1000;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Kick(container.get(PlayerService.class));
        gamePlayer(true).account().grant(Permission.values());
    }

    @Test
    void kickNone() throws AdminException, SQLException {
        execute("kick", "--all");

        assertOutput("0 player(s) kicked from server");
        assertTrue(session.isAlive());
    }

    @Test
    void kickAll() throws SQLException, NoSuchFieldException, AdminException, IllegalAccessException {
        GamePlayer[] players = new GamePlayer[] {
            createPlayerWithIp(null),
            createPlayerWithIp(null),
            createPlayerWithIp(null),
        };

        execute("kick", "--all");

        assertOutput("3 player(s) kicked from server");

        for (GamePlayer player : players) {
            GameSession session = getSession(player);
            SendingRequestStack stack = new SendingRequestStack((DummyChannel) session.channel());

            assertFalse(session.isAlive());
            stack.assertLast(ServerMessage.kick("bob", ""));
        }
    }

    @Test
    void kickAllShouldIgnoreGameMaster() throws SQLException, NoSuchFieldException, AdminException, IllegalAccessException {
        GamePlayer simplePlayer = createPlayerWithIp(null);
        GamePlayer gameMaster = createPlayerWithIp(null);
        gameMaster.account().grant(Permission.ACCESS);

        execute("kick", "--all");

        assertOutput("1 player(s) kicked from server");

        assertFalse(getSession(simplePlayer).isAlive());
        assertTrue(getSession(gameMaster).isAlive());
    }

    @Test
    void kickByIp() throws SQLException, NoSuchFieldException, IllegalAccessException, AdminException {
        GamePlayer match = createPlayerWithIp("12.34.56.78");
        GamePlayer notMatch = createPlayerWithIp("14.25.36.78");

        execute("kick", "--ip", "12.34.56.78");
        assertOutput("1 player(s) kicked from server");

        assertFalse(getSession(match).isAlive());
        assertTrue(getSession(notMatch).isAlive());
    }

    @Test
    void kickByIpMask() throws SQLException, NoSuchFieldException, IllegalAccessException, AdminException {
        GamePlayer gp1 = createPlayerWithIp("12.34.56.78");
        GamePlayer gp2 = createPlayerWithIp("12.34.14.23");
        GamePlayer gp3 = createPlayerWithIp("14.25.36.78");

        execute("kick", "--ip", "12.34.0.0/16");
        assertOutput("2 player(s) kicked from server");

        assertFalse(getSession(gp1).isAlive());
        assertFalse(getSession(gp2).isAlive());
        assertTrue(getSession(gp3).isAlive());
    }

    @Test
    void missingFilter() {
        assertThrowsWithMessage(CommandException.class, "At least one filter must be defined, or use --all to kick all players", () -> execute("kick"));
    }

    @Test
    void kickWithMessage() throws SQLException, NoSuchFieldException, AdminException, IllegalAccessException {
        GamePlayer[] players = new GamePlayer[] {
            createPlayerWithIp(null),
            createPlayerWithIp(null),
            createPlayerWithIp(null),
        };

        execute("kick", "--all", "Hello", "World", "!");

        assertOutput("3 player(s) kicked from server");

        for (GamePlayer player : players) {
            GameSession session = getSession(player);
            SendingRequestStack stack = new SendingRequestStack((DummyChannel) session.channel());

            assertFalse(session.isAlive());
            stack.assertLast(ServerMessage.kick("bob", "\nHello World !"));
        }
    }

    @Test
    void help() {
        assertHelp(
            "kick - Kick many players from current game server",
            "========================================",
            "SYNOPSIS",
                "\tkick [options] [MESSAGE]",
            "OPTIONS",
                "\tMESSAGE : The message to send. Must be defined after all options.",
                "\t--all : Kick all players",
                "\t--ip : Kick by IP address mask",
            "Note: At least one option is required. Use --all to kick all players.",
            "EXAMPLES",
                "\tkick --all             - Kick all players (expect game masters)",
                "\tkick --ip 15.18.0.0/16 - Kick all players from an ip mask",
                "\tkick --ip 15.18.1.23   - Kick all players from an ip",
            "SEE ALSO",
                "\tbanip - For ban an IP address",
                "\t!kick - For kick a player",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }

    private GamePlayer createPlayerWithIp(String ipAddress) throws SQLException {
        return makeSimpleGamePlayer(++id, server.createSession(ipAddress), true);
    }

    private GameSession getSession(GamePlayer player) throws IllegalAccessException, NoSuchFieldException {
        Field field = GamePlayer.class.getDeclaredField("session");
        field.setAccessible(true);

        return (GameSession) field.get(player);
    }
}

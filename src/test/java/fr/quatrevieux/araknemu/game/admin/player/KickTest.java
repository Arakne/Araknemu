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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class KickTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void cannotKickSelf() throws SQLException {
        command = new Kick(gamePlayer(true));

        assertThrowsWithMessage(CommandException.class, "Cannot kick yourself", () -> execute("kick"));
    }

    @Test
    void kickWithoutMessage() throws Exception {
        GamePlayer target = makeOtherPlayer();

        Field sessionField = GamePlayer.class.getDeclaredField("session");
        sessionField.setAccessible(true);

        GameSession session = (GameSession) sessionField.get(target);
        SendingRequestStack targetStack = new SendingRequestStack((DummyChannel) session.channel());

        command = new Kick(target);

        execute("kick");

        targetStack.assertLast(ServerMessage.kick("bob", ""));
        assertFalse(session.isAlive());
    }

    @Test
    void kickWithMessage() throws Exception {
        GamePlayer target = makeOtherPlayer();

        Field sessionField = GamePlayer.class.getDeclaredField("session");
        sessionField.setAccessible(true);

        GameSession session = (GameSession) sessionField.get(target);
        SendingRequestStack targetStack = new SendingRequestStack((DummyChannel) session.channel());

        command = new Kick(target);

        execute("kick", "Last", "warning");

        targetStack.assertLast(ServerMessage.kick("bob", "\nLast warning"));
        assertFalse(session.isAlive());
    }
}

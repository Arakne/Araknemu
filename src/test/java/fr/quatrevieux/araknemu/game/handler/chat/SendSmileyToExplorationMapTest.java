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

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.chat.UseSmiley;
import fr.quatrevieux.araknemu.network.game.out.chat.Smiley;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SendSmileyToExplorationMapTest extends GameBaseCase {
    @Test
    void notExploring()  {
        assertThrows(CloseImmediately.class, () -> handlePacket(new UseSmiley(3)));
    }

    @Test
    void inExploration() throws Exception {
        explorationPlayer();

        handlePacket(new UseSmiley(3));

        requestStack.assertLast(new Smiley(explorationPlayer(), 3));
    }

    @Test
    void spamCheck() throws Exception {
        explorationPlayer();

        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        requestStack.clear();

        try {
            handlePacket(new UseSmiley(3));
        } catch (ErrorPacket error) {
            assertEquals(ServerMessage.spam().toString(), error.packet().toString());
        }

        requestStack.assertEmpty();
    }
}

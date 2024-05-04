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

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.network.game.in.chat.UseSmiley;
import fr.quatrevieux.araknemu.network.game.out.chat.Smiley;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SendSmileyToFightTest extends FightBaseCase {
    @Test
    void inFight() throws Exception {
        createFight();

        handlePacket(new UseSmiley(3));

        requestStack.assertLast(new Smiley(session.fighter(), 3));
    }

    @Test
    void spamCheck() throws Exception {
        createFight();

        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        handlePacket(new UseSmiley(3));
        requestStack.clear();

        handlePacket(new UseSmiley(3));
        requestStack.assertAll(ServerMessage.spam());
    }
}

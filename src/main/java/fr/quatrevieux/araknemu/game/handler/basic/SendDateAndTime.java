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

package fr.quatrevieux.araknemu.game.handler.basic;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerTime;

import java.util.Calendar;

/**
 * Send the current server date
 */
final public class SendDateAndTime implements PacketHandler<GameSession, AskDate> {
    @Override
    public void handle(GameSession session, AskDate packet) throws Exception {
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.YEAR, -1370);

        session.send(new ServerDate(calendar));
        session.send(new ServerTime(calendar));
    }

    @Override
    public Class<AskDate> packet() {
        return AskDate.class;
    }
}

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

package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Parse packet for leave or kick fighter
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L27
 */
public final class KickOrLeaveRequestParser implements SinglePacketParser<Packet> {
    @Override
    public Packet parse(String input) throws ParsePacketException {
        return input.isEmpty()
            ? new LeaveFightRequest()
            : new KickFighterRequest(Integer.parseInt(input))
        ;
    }

    @Override
    public @MinLen(2) String code() {
        return "GQ";
    }
}

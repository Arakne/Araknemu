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
import fr.quatrevieux.araknemu.util.ParseUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Show a cell on fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L77
 */
public final class ShowCellRequest implements Packet {
    private final @NonNegative int cellId;

    public ShowCellRequest(@NonNegative int cellId) {
        this.cellId = cellId;
    }

    /**
     * The shown cell id
     */
    public @NonNegative int cellId() {
        return cellId;
    }

    public static final class Parser implements SinglePacketParser<ShowCellRequest> {
        @Override
        public ShowCellRequest parse(String input) throws ParsePacketException {
            return new ShowCellRequest(ParseUtils.parseNonNegativeInt(input));
        }

        @Override
        public @MinLen(2) String code() {
            return "Gf";
        }
    }
}

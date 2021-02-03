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

package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Request an exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L23
 */
public final class ExchangeRequest implements Packet {
    private final ExchangeType type;
    private final Integer id;
    private final Integer cell;

    public ExchangeRequest(ExchangeType type, Integer id, Integer cell) {
        this.type = type;
        this.id = id;
        this.cell = cell;
    }

    /**
     * Get the exchange type
     */
    public ExchangeType type() {
        return type;
    }

    /**
     * Get the target id
     * May be not provided
     */
    public Optional<Integer> id() {
        return Optional.ofNullable(id);
    }

    /**
     * Get the target cell
     * May be not provided
     */
    public Optional<Integer> cell() {
        return Optional.ofNullable(cell);
    }

    public static final class Parser implements SinglePacketParser<ExchangeRequest> {
        @Override
        public ExchangeRequest parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.splitPreserveAllTokens(input, "|", 3);

            if (parts.length < 2) {
                throw new ParsePacketException(code() + input, "Exchange request must have at least two parts");
            }

            final int typeId = Integer.parseInt(parts[0]);

            if (typeId < 0 || typeId >= ExchangeType.values().length) {
                throw new ParsePacketException(code() + input, "Invalid exchange type");
            }

            return new ExchangeRequest(
                ExchangeType.values()[typeId],
                !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : null,
                parts.length == 3 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) : null
            );
        }

        @Override
        public String code() {
            return "ER";
        }
    }
}

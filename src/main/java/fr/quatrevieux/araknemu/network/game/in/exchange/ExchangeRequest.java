package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Request an exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L23
 */
final public class ExchangeRequest implements Packet {
    final static public class Parser implements SinglePacketParser<ExchangeRequest> {
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

    final private ExchangeType type;
    final private Integer id;
    final private Integer cell;

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
}

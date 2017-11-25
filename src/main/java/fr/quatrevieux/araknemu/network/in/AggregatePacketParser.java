package fr.quatrevieux.araknemu.network.in;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base packet parser
 * Will aggregate multiple {@link SinglePacketParser} for parse the incoming packet
 */
final public class AggregatePacketParser implements PacketParser {
    final private ConcurrentMap<String, SinglePacketParser> parsers = new ConcurrentHashMap<>();

    private int minCodeLength = 2;
    private int maxCodeLength = 2;

    public AggregatePacketParser(SinglePacketParser[] parsers) {
        for (SinglePacketParser parser : parsers) {
            this.register(parser);
        }
    }

    public AggregatePacketParser(Collection<SinglePacketParser> parsers) {
        for (SinglePacketParser parser : parsers) {
            this.register(parser);
        }
    }

    @Override
    public Packet parse(String input) throws ParsePacketException {
        int len = maxCodeLength > input.length() ? input.length() : maxCodeLength;

        for(; len >= minCodeLength; --len) {
            String header = input.substring(0, len);

            if(parsers.containsKey(header)){
                return parsers
                    .get(header)
                    .parse(input.substring(len))
                ;
            }
        }

        throw new UndefinedPacketException(input);
    }

    /**
     * Register a new parse
     * If a parse with the same code is already registered, it will be override
     * @param parser New parser
     */
    public void register(SinglePacketParser parser) {
        parsers.put(parser.code(), parser);

        if (parser.code().length() < minCodeLength) {
            minCodeLength = parser.code().length();
        }

        if (parser.code().length() > maxCodeLength) {
            maxCodeLength = parser.code().length();
        }
    }
}

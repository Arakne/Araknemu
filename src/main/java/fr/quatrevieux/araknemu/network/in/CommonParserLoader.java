package fr.quatrevieux.araknemu.network.in;

import java.util.Arrays;
import java.util.Collection;

/**
 * Loader for commons input packet parsers
 */
final public class CommonParserLoader implements ParserLoader {
    @Override
    public Collection<SinglePacketParser> load() {
        return Arrays.asList(
            new PingResponse.Parser(),
            new AskQueuePosition.Parser()
        );
    }
}

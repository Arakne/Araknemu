package fr.quatrevieux.araknemu.network.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Aggregate of parser loaders
 */
final public class AggregateParserLoader implements ParserLoader {
    final private ParserLoader[] loaders;

    public AggregateParserLoader(ParserLoader[] loaders) {
        this.loaders = loaders;
    }

    @Override
    public Collection<SinglePacketParser> load() throws IOException {
        Collection<SinglePacketParser> parsers = new ArrayList<>();

        for (ParserLoader loader : loaders) {
            parsers.addAll(loader.load());
        }

        return parsers;
    }
}
